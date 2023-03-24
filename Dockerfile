FROM amd64/maven:3.6.3-openjdk-14-slim
WORKDIR /tests
COPY . .
CMD mvn clean test