#!/bin/bash
java $JAVA_OPTS \
    -Dserver.port=$PORT \
    -Deureka.client.service-url.defaultZone=https://greatappxyz-discovery-service.herokuapp.com/eureka/ \
    -Deureka.instance.hostname=database.greatapp.xyz \
    -Deureka.instance.prefer-ip-address=false \
    -Dspring.cloud.config.uri=https://greatappxyz-configuration-service.herokuapp.com/ \
    -jar app.jar \
