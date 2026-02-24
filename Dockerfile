# Runtime Dockerfile for crm-engine
# Build the distribution first: ./gradlew installDist

FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="NekGambling"
LABEL description="iGambling CRM Engine Service"

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy pre-built distribution
COPY build/install/crm-engine/ /app/

# Set permissions
RUN chmod +x /app/bin/crm-engine && \
    chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose HTTP port
EXPOSE 8080

CMD ["/app/bin/crm-engine"]
