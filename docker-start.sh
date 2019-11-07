#!/bin/bash

java \
  -Denv="${ENV}" \
  -DhostAddress="${HOST_ADDRESS}" \
  -Dport="${PORT}" \
  -DmongoHost="${MONGO_HOST}" \
  -DmongoPort="${MONGO_PORT}" \
  -DmongoUser="${MONGO_USER}" \
  -DmongoPassword="${MONGO_PASSWORD}" \
  -DmongoDbName="${MONGO_DB_NAME}" \
  -DpublicUri="${PUBLIC_URI}" \
  -jar /usr/src/app/app.jar
