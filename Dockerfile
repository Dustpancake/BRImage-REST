FROM python:3.7-slim AS installer
RUN apt-get update \
	&& apt-get install gcc -y \
	&& apt-get clean
RUN pip install --user BRImage

FROM python:3.7-slim AS python
COPY --from=installer /root/.local /root/.local

FROM openjdk:8-jdk-alpine
COPY --from=python / /
ENV PATH=/root/.local/bin:$PATH

#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
COPY ./build/libs/rest-service-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]