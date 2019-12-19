FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.5_10-slim as TEMP_BUILDER
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY library/build.gradle library/settings.gradle library/gradlew  $APP_HOME/library/
COPY library/gradle $APP_HOME/library/gradle
RUN (cd $APP_HOME/library && ./gradlew build) || return 0
COPY library/src $APP_HOME/library/src
RUN cd $APP_HOME/library && ./gradlew build

COPY processer/build.gradle processer/settings.gradle processer/gradlew  $APP_HOME/processer/
COPY processer/gradle $APP_HOME/processer/gradle
RUN (cd $APP_HOME/processer && ./gradlew build) || return 0
COPY processer/src $APP_HOME/processer/src
RUN cd $APP_HOME/processer && ./gradlew build

FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.5_10-slim
COPY --from=TEMP_BUILDER /usr/app/processer/build/libs/processor.jar /processor.jar
CMD ["java","-Dserver.port=${PORT}","-jar","/processor.jar"]