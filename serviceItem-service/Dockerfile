FROM jenkins-gradle-mvn-openjdk11:latest
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java"," $JAVA_OPTS -jar","/app.jar"]
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
