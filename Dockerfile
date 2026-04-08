# ─────────────────────────────────────────
# Stage 1: Build
# Gradle 7.2 requer Java 11 ou 16 — não suporta Java 17+
# ─────────────────────────────────────────
FROM eclipse-temurin:11-jdk AS builder

WORKDIR /app

# Copia os arquivos de build do Gradle primeiro (melhor uso de cache)
COPY gradlew .
COPY gradle gradle
COPY build.gradle* .
COPY settings.gradle* .

# Corrige line endings CRLF→LF e garante permissão de execução
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Baixa dependências antecipadamente (camada cacheável)
RUN ./gradlew dependencies --no-daemon || true

# Copia o código-fonte e gera o JAR
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# ─────────────────────────────────────────
# Stage 2: Runtime (imagem enxuta)
# ─────────────────────────────────────────
FROM eclipse-temurin:11-jre AS runtime

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
