#!/bin/bash
./gradlew build -x test
java -jar build/libs/database-service-0.0.1-SNAPSHOT.jar
