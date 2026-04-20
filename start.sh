#!/bin/bash

echo "Starting Employee Management Application..."
echo "============================================="

# Start Spring Boot API
echo "[1/2] Starting Spring Boot API..."
mvn spring-boot:run &
API_PID=$!
echo "API PID: $API_PID"

# Wait for API to start
echo "Waiting for API to start (20 seconds)..."
sleep 20

# Start SPA
echo "[2/2] Starting SPA (Vite dev server)..."
cd spa && npm run host &
SPA_PID=$!
echo "SPA PID: $SPA_PID"

echo ""
echo "============================================="
echo "Both services are starting..."
echo "API PID: $API_PID"
echo "SPA PID: $SPA_PID"
echo ""
echo "Press Ctrl+C to stop all services"

# Handle cleanup on exit
trap "kill $API_PID $SPA_PID 2>/dev/null; exit" INT TERM

wait
