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

# --- Stands ---
# Both stands run docker-compose.yaml with the same delivery-app image and publish the same host ports, so only one
# runs at a time. The dev stand (`make run`) keeps its data. The suites of tests/ run on the test stand: a compose
# project of its own (containers, network and volumes delivery-test_*), started from scratch by every suite and left
# up afterwards, so its data can be inspected
TEST_STAND := $(COMPOSE_CMD) -p delivery-test
# Detekt first: Gradle runs the tasks in command-line order, so a finding fails the build before anything is compiled
BUILD_JAR := $(GRADLEW) detekt build bootJar -x test

# First lines of a suite: refuses to run beside the dev stand, builds the image of the current sources, removes what
# an earlier run left in the test stand and starts it again, waiting until every service is healthy
define START_TEST_STAND
$(if $(shell $(COMPOSE_CMD) ps -q 2> $(NULL_DEVICE)),$(error The dev stand is running and holds the ports of the test stand: run 'make stop' first))
$(BUILD_JAR)
$(DEV_ENVS) $(COMPOSE_CMD) build app
$(TEST_STAND) down -v
$(TEST_STAND) up -d --wait
endef

.PHONY: test test-smoke test-e2e test-load run stop compile clean clean-docker-data

# Detekt, then unit and integration tests (*IT need Docker): a Detekt finding fails before any test runs.
# --rerun: run tests even when Gradle considers them up-to-date (otherwise an unchanged build skips them)
test:
	$(GRADLEW) detekt test --rerun

# Smoke tests against the app of a clean test stand at http://localhost:8080
test-smoke:
	$(START_TEST_STAND)
	$(GRADLEW) :tests:smoke:smokeTest

# End-to-end tests through the Kafka (localhost:9092) and Postgres (localhost:5432) of a clean test stand
test-e2e:
	$(START_TEST_STAND)
	$(GRADLEW) :tests:e2e:e2eTest

# Gatling load tests against a clean test stand at http://localhost:8080, reading the inbox lag from its Postgres at
# localhost:5432. Fails when a simulation breaks its SLO; HTML reports: tests/load/build/reports/gatling
test-load:
	$(START_TEST_STAND)
	$(GRADLEW) :tests:load:gatlingRun --all

# Builds and starts the dev stand, removing the test stand that holds the same ports.
# Returns once every service is healthy (the app: /actuator/health is UP), so tests can start right after it
run:
	$(BUILD_JAR)
	$(TEST_STAND) down -v
	$(DEV_ENVS) $(COMPOSE_CMD) up --build -d --wait

# Stops both stands (the data in their volumes is kept)
stop:
	$(COMPOSE_CMD) stop
	$(TEST_STAND) stop

compile:
	$(GRADLEW) compileKotlin compileTestKotlin

# Removes the Gradle build output (**/build); Docker containers and data are left alone
clean:
	$(GRADLEW) clean

# Removes the containers, network and ALL volumes of both stands, including the Postgres and Kafka data of the dev stand
clean-docker-data:
	$(COMPOSE_CMD) down -v
	$(TEST_STAND) down -v
