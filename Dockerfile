FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="NekGambling"
LABEL description="iGambling CRM Engine Service"

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY build/install/crm-engine/ /app/

RUN chmod +x /app/bin/crm-engine && \
    chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

CMD ["/app/bin/crm-engine"]
