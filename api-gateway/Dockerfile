# =====================================================
# STAGE 1: BUILD - Maven se JAR file banao
# =====================================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Pehle sirf pom.xml copy karo (dependency caching ke liye)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ab baaki source code copy karo aur build karo
COPY src ./src
RUN mvn clean package -DskipTests -B

# =====================================================
# STAGE 2: RUN - Sirf JRE chahiye, full JDK nahi
# =====================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Builder stage se JAR copy karo
COPY --from=builder /app/target/*.jar app.jar

# Port expose karo
EXPOSE 8080

# Application start karo
ENTRYPOINT ["java", "-jar", "app.jar"]
