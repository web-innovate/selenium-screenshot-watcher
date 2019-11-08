FROM webinnovate/docker-maven3-jdk8-node7 as build

WORKDIR /usr/src/app

COPY . .

RUN mvn -B clean install -Denv=test

FROM anapsix/alpine-java:8u202b08_jdk as release

WORKDIR /usr/src/app

ENV ENV=docker
ENV HOST_ADDRESS=http://0.0.0.0
ENV PUBLIC_URI=docker
ENV PORT=80
ENV MONGO_HOST=test
ENV MONGO_PORT=test
ENV MONGO_USER=test
ENV MONGO_PASSWORD=test
ENV MONGO_DB_NAME=test


COPY --from=build /usr/src/app/target/selenium-screenshot-watcher-1.0.2.jar ./app.jar
COPY --from=build /usr/src/app/docker-start.sh ./start.sh

EXPOSE 80

CMD ["./start.sh"]
