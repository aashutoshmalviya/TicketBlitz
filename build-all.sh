#!/bin/bash

# Exit immediately if any command fails
set -e

echo " Starting TicketBlitz Monorepo Build..."
echo "========================================"

# 1. Build Shared Libraries FIRST (Crucial for dependencies)

# 2. Build the API Gateway (Root Level)
echo "Phase 1: Building API Gateway..."
cd api-gateway
mvn clean package -DskipTests
cd ..
echo "Phase 1 Complete."
echo "----------------------------------------"

# 3. Build the Domain Services (Inside 'services/' folder)
echo "🔨 Phase 2: Building Domain Microservices..."
SERVICES=(
    "auth-service"
    "catalog-service"
    "booking-service"
    "payment-service"
    "notification-service"
)

for SERVICE in "${SERVICES[@]}"; do
    echo " -> Packaging services/$SERVICE..."
    cd "services/$SERVICE"
    mvn clean package -DskipTests
    cd ../..
done
echo " Phase 2 Complete."
echo "========================================"
echo "SUCCESS! All microservices are built and ready."
echo "JAR files are located in their respective /target directories."