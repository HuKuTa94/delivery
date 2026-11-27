# --- Detect OS and choose Gradle wrapper script ---
ifeq ($(OS),Windows_NT)
    GRADLEW := .\gradlew.bat
    SHELL := cmd
    NULL_DEVICE := NUL
    DEV_ENVS := set USER_ID=197609 && set GROUP_ID=197609 &&
else
    GRADLEW := ./gradlew
    SHELL := /bin/bash
    NULL_DEVICE := /dev/null
    DEV_ENVS := USER_ID=$(shell id -u) GROUP_ID=$(shell id -g)
endif

# --- Detect container runtime ---
# Use 'docker compose' (v2) if available, fallback to 'docker-compose' (v1)
ifeq ($(shell docker compose version 2> $(NULL_DEVICE)),)
	COMPOSE_CMD := docker-compose
else
	COMPOSE_CMD := docker compose
endif

.PHONY: build test test-smoke test-e2e test-load clean clean-docker-data

# --rerun: run tests even when Gradle considers them up-to-date (otherwise an unchanged build skips them)
test:
	$(GRADLEW) test --rerun detekt

# Smoke tests against the running application at http://localhost:8080 (start it with `make run`)
test-smoke:
	$(GRADLEW) :tests:smoke:smokeTest

# End-to-end tests against the running application, through its Kafka (localhost:9092) and Postgres (localhost:5432)
# (start it with `make run`)
test-e2e:
	$(GRADLEW) :tests:e2e:e2eTest

# Gatling load tests against the running application at http://localhost:8080 and its Postgres at localhost:5432
# (start it with `make run`). Fails when a simulation breaks its SLO; HTML reports: tests/load/build/reports/gatling.
# Every run adds orders to the Postgres volume (`make clean-docker-data` resets it)
test-load:
	$(GRADLEW) :tests:load:gatlingRun --all

# Returns once every service is healthy (the app: /actuator/health is UP), so tests can start right after it
run: build
	$(DEV_ENVS) $(COMPOSE_CMD) up --build -d --wait

stop:
	$(COMPOSE_CMD) stop

# Removes the Gradle build output (**/build); Docker containers and data are left alone
clean:
	$(GRADLEW) clean

# Removes the compose containers, network and ALL volumes, including the Postgres and Kafka data
clean-docker-data:
	$(COMPOSE_CMD) down -v

build:
	$(GRADLEW) build bootJar -x test -x detekt

detekt:
	$(GRADLEW) detekt

compile:
	$(GRADLEW) compileKotlin compileTestKotlin