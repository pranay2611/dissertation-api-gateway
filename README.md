# API Gateway

Central entry point for all microservices with routing, authentication, and resilience patterns.

## Features
- Spring Cloud Gateway for routing
- JWT authentication filter
- Circuit breaker with Resilience4j
- Rate limiting with Redis
- Global CORS configuration
- Request/response logging
- Fallback endpoints
- Retry mechanism
- Health checks and monitoring

## Routes

### Public Routes (No Authentication)
- POST /api/auth/register
- POST /api/auth/login
- GET /api/auth/user/{username}

### Protected Routes (Requires JWT)
- All /api/orders/** endpoints
- All /api/payments/** endpoints
- All /api/notifications/** endpoints

## Circuit Breaker Configuration
- Sliding window size: 10 requests
- Failure rate threshold: 50%
- Wait duration in open state: 5 seconds
- Automatic transition to half-open state

## Rate Limiting
- Replenish rate: 10 requests/second
- Burst capacity: 20 requests

## Testing
```bash
# Health check
curl http://localhost:8080/health

# Login (get JWT token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password"}'

# Use token for protected endpoints
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Dependencies
- Redis (for rate limiting)
- All microservices running on their respective ports

## Run Locally
```bash
# Start Redis
docker run -d -p 6379:6379 redis:7-alpine

# Start gateway
mvn spring-boot:run
```

## Run Tests
```bash
mvn test
```

## Docker Compose
```bash
docker-compose up --build
```

Gateway runs on port 8080

## Monitoring
Access gateway actuator endpoints:
- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/gateway/routes

