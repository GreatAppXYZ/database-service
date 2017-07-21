echo "::::::::::STARTING DATABASE SERVICE:::::::::::"
cd %~dp0
call gradlew build -x test
call java -jar build/libs/database-service-0.0.1-SNAPSHOT.jar
