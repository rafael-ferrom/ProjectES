# Estágio 1: Build da aplicação com uma imagem que inclui Maven e JDK Temurin
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app

# >>>>> ALTERAÇÃO PRINCIPAL AQUI <<<<<
# Em vez de copiar TUDO do contexto, copie apenas o conteúdo da pasta 'backend'
# para o diretório de trabalho atual (/app) do contêiner.
COPY backend/ .

# Agora o 'mvn' será executado no diretório /app, que conterá o pom.xml
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação (sem alterações)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]