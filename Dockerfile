# Estágio 1: Build da aplicação com Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Estágio 2: Criação da imagem final com o JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app