FROM webinnovate/docker-maven3-jdk8-node7 as build

WORKDIR /usr/src/app

COPY . .

RUN mvn -B clean install -Denv=default

FROM anapsix/alpine-java:8u202b08_jdk

WORKDIR /usr/src/app

ENV PORT=80

ENV ENV=docker
ENV PUBLIC_URI=docker

COPY --from=build /usr/src/app/target/selenium-screenshot-watcher-1.0.1.jar ./app.jar

EXPOSE 80
CMD ["/usr/bin/java \
    -Denv=${ENV} \
    -DpublicUri=${PUBLIC_URI} \
    -jar /usr/src/app/app.jar \
"]

