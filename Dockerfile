FROM        ubuntu:latest
EXPOSE      25575
RUN         apt update && apt install -y openjdk-17-jdk maven
COPY        . /chat
RUN         cd /chat && mvn package
ENTRYPOINT  java -jar /chat/target/chat-server-1.0-SNAPSHOT-jar-with-dependencies.jar
