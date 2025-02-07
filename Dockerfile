# 第一阶段：可以省略，因为不再需要在容器内进行 Maven 打包
# FROM maven:3.8.4 AS build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# RUN mvn package -DskipTests

# 直接使用 OpenJDK 17 作为运行时环境
FROM openjdk:17-jdk-slim

WORKDIR /app

# 直接从本地复制打好的 JAR 文件到容器中
COPY friendsMatching-0.0.1-SNAPSHOT.jar app.jar

# 设置启动命令
#ENTRYPOINT ["java", "-jar",  "app.jar","--spring.profiles.active=prod"]

# Run the web service on container startup.
CMD ["java","-jar","app.jar","--spring.profiles.active=prod"]