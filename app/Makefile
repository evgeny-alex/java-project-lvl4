.DEFAULT_GOAL := build-run

run-dist:
	/Documents/Job/Hexlet/java/java-project-lvl4/build/install/app/bin/app

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean install

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

update-deps:
	./gradlew useLatestVersions

build-run: build run

.PHONY: build