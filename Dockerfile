# Estágio 1: Build com Maven
# Usamos uma imagem que já contém o Maven e o JDK para compilar nosso projeto.
FROM maven:3.8.5-openjdk-17 AS builder

# Define o diretório de trabalho dentro do contêiner.
WORKDIR /app

# Copia o pom.xml primeiro para aproveitar o cache do Docker.
# Se o pom.xml não mudar, o Docker não baixará as dependências novamente.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o resto do código-fonte.
COPY src ./src

# Executa o build do Maven, pulando os testes para acelerar o processo.
# O resultado será um arquivo .jar em /app/target/
RUN mvn package -DskipTests

# Estágio 2: Imagem final
# Usamos uma imagem leve, contendo apenas o Java Runtime Environment (JRE).
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copia o arquivo .jar gerado no estágio anterior para a nossa imagem final.
# Verifique o nome do seu .jar na pasta 'target' após o build.
COPY --from=builder /app/target/flop-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que a aplicação Spring Boot usa.
EXPOSE 8080

# Comando para iniciar a aplicação quando o contêiner for executado.
ENTRYPOINT ["java", "-jar", "app.jar"]