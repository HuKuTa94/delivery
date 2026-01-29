# --- Detect OS and choose Gradle wrapper script ---
ifeq ($(OS),Windows_NT)
    GRADLEW := gradlew.bat
    SHELL := cmd
    DOCKER_IMAGE ?= delivery-app
    DEV_ENVS := set USER_ID=197609 && set GROUP_ID=197609 &&
else
    GRADLEW := ./gradlew
    SHELL := /bin/bash
    DEV_ENVS := USER_ID=$(shell id -u) GROUP_ID=$(shell id -g)
endif

# --- Detect container runtime ---
# Use 'docker compose' (v2) if available, fallback to 'docker-compose' (v1)
ifeq ($(shell docker compose version 2> /dev/null),)
	COMPOSE_CMD := docker-compose
else
	COMPOSE_CMD := docker compose
endif

.PHONY: build test

test:
	$(GRADLEW) test detekt

run: build
	$(DEV_ENVS) docker compose up --build -d

stop:
	$(COMPOSE_CMD) stop

clean:
	$(COMPOSE_CMD) down

build:
	$(GRADLEW) build bootJar -x test -x detekt

detekt:
	$(GRADLEW) detekt

compile:
	$(GRADLEW) compileKotlin compileTestKotlin