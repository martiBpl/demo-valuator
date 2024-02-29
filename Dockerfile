ARG BASE_IMAGE_REPO_URI="docker.io"
FROM $BASE_IMAGE_REPO_URI/eclipse-temurin:17-jdk AS build

WORKDIR app
COPY . /app
RUN ./gradlew --no-daemon app:build --info --stacktrace -x test

RUN cp \
    /app/scripts/analyze-deps.sh \
    /app/scripts/build-jre.sh \
    /app/app/build/libs/app.jar \
    /app
RUN ./build-jre.sh app.jar /javaruntime

FROM $BASE_IMAGE_REPO_URI/debian:buster-slim AS run

RUN groupadd -g 1000 app && \
    useradd -r -u 1000 -g app app

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=build --chown=app:app /javaruntime $JAVA_HOME

VOLUME /tmp
RUN mkdir /app && chown app:app /app
USER app
WORKDIR /app

RUN mkdir ./agent
COPY --from=build --chown=app:app /app/app/build/libs/app.jar /app/app.jar

CMD ["sh", "-c", "java -jar app.jar"]
