# 🔗 TinyURL Service

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)
![Redis](https://img.shields.io/badge/Redis-7.2-red.svg)
![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)

A production-ready, distributed **URL Shortener** built with **Spring Boot**, **MongoDB**, **Redis**, and **Zookeeper**, packaged with Docker for easy deployment.  

This project demonstrates **real-world system design patterns** including:
- 🎯 **Distributed token generation** with range allocation
- 🔄 **Consistent hashing** for load distribution
- ⚡ **Redis caching** for high-performance lookups
- 🔐 **Zookeeper coordination** for distributed consensus

> Inspired by [System Design - URL Shortening](https://systemdesign.one/url-shortening-system-design/#further-system-design-learning-resources)

---

## 📑 Table of Contents
- [Architecture](#-architecture)
- [Features](#-features)
- [Tech Stack](#️-tech-stack)
- [Getting Started](#-getting-started)
- [API Usage](#-api-usage)
- [Load Testing](#-load-testing)
- [Troubleshooting](#-troubleshooting)
- [Related Work](#-related-work)

---

## 📸 Architecture
![tinyurl](https://github.com/user-attachments/assets/7f977690-aa4c-49fa-ace0-013e3bd0dc39)

The system uses a **microservices architecture** with:
- **Token Service**: Multiple instances generate unique short codes using pre-allocated ranges
- **Token Range Service**: Manages token range distribution to prevent collisions
- **Redis**: Acts as a distributed cache layer for fast URL lookups
- **MongoDB**: Persistent storage for URL mappings
- **Zookeeper**: Coordinates distributed token range allocation

---

## ✨ Features

- 🚀 **High Performance**: Redis caching ensures sub-millisecond response times
- 📈 **Horizontally Scalable**: Spin up multiple service instances effortlessly
- 🔒 **Collision-Free**: Distributed token generation with range-based allocation
- ⚡ **Fast Lookups**: Base62 encoding for compact, readable short URLs
- 🐳 **Docker Ready**: One-command deployment with Docker Compose
- 🔄 **Load Balanced**: Consistent hashing for even request distribution
- 🛡️ **Production Ready**: Global exception handling and comprehensive error responses
- 📊 **Observable**: Structured logging and monitoring-ready

---

## 🛠️ Tech Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Backend** | Spring Boot 3.x | REST API framework |
| **Database** | MongoDB 7.0 | URL mappings persistence |
| **Cache** | Redis 7.2 | High-speed in-memory cache |
| **Coordination** | Apache Zookeeper | Distributed synchronization |
| **Encoding** | Base62 | Short URL generation |
| **Containerization** | Docker & Docker Compose | Service orchestration |

---

## 🚀 Getting Started

### Prerequisites
- Docker installed on your machine
- Basic knowledge of curl or any API testing tool (like Postman)

---

### 🏗️ Setup & Run

1. **Build the Token Service**
   ```bash
   cd tokenservice
   docker build -t tokenservice:latest .
   ```

2. **Start all services**  
   From the project root directory:
   ```bash
   docker compose up -d
   ```

3. **Services will be available on:**
    - http://localhost:9001 (Token Service Instance 1)
    - http://localhost:9002 (Token Service Instance 2)

   > Ports are configurable in docker-compose.yml.

---

### 🔑 API Usage

#### 1. Shorten a URL
```bash
curl --location 'http://localhost:9001/api/v1/shorten' \
  --header 'Content-Type: application/json' \
  --data '{
    "longUrl": "https://example.com"
  }'
```

**✅ Response:**
```json
{
  "shortUrl": "http://localhost:9001/api/v1/4j0",
  "longUrl": "https://example.com"
}
```

---

#### 2. Expand a Short URL
```bash
curl --location 'http://localhost:9001/api/v1/4j0'
```

**✅ Response:**
```json
{
  "longUrl": "https://example.com"
}
```

> **Note:** The service automatically redirects to the long URL when accessed via browser.

---

## 🧪 Load Testing

Test the system under load with parallel requests across multiple service instances:

```bash
# Send 1000 concurrent requests to instance 1
seq 1 1000 | xargs -n 1 -P 100 -I {} curl --location 'http://localhost:9001/api/v1/shorten' \
  --header 'Content-Type: application/json' \
  --data '{"longUrl":"https://example.com"}' &

# Send 1000 concurrent requests to instance 2
seq 1 1000 | xargs -n 1 -P 100 -I {} curl --location 'http://localhost:9002/api/v1/shorten' \
  --header 'Content-Type: application/json' \
  --data '{"longUrl":"https://example.com"}' &

wait
```

**What to observe:**
- No duplicate short URLs generated (collision-free)
- Consistent response times under load
- Proper token range distribution across instances

---

## 🔧 Troubleshooting

### Services won't start
```bash
# Check if containers are running
docker compose ps

# View service logs
docker compose logs -f tokenservice
docker compose logs -f mongodb
docker compose logs -f redis
```

### Port conflicts
If ports 9001 or 9002 are already in use, modify the `docker-compose.yml` file:
```yaml
ports:
  - "9003:8080"  # Change to an available port
```

### MongoDB connection issues
Ensure MongoDB is fully initialized before the token service starts:
```bash
# Restart services in order
docker compose down
docker compose up -d mongodb redis zookeeper
sleep 10
docker compose up -d
```

### Clear all data and restart
```bash
docker compose down -v  # -v removes volumes
docker compose up -d
```

---

## 📚 Related Work

Explore more system design implementations:
- 🔄 **[Consistent Hashing](https://github.com/VaibhavKVerma/ConsistentHashing)** - Load balancer simulation with consistent hashing algorithm
- 🌸 **[Bloom Filter](https://github.com/VaibhavKVerma/BloomFilter)** - Probabilistic data structure for efficient membership testing

---

## 📝 Configuration

The service supports multiple Spring profiles:
- `dev` - Development environment
- `docker` - Docker/containerized deployment
- `prod` - Production environment

Configure via `application-{profile}.properties` files in `src/main/resources/`.

---

## 🎉 Success!

Your distributed TinyURL service is now up and running! 🚀

**Next Steps:**
- Try the API endpoints with different URLs
- Run load tests to see horizontal scaling in action
- Monitor Redis cache hits and MongoDB writes
- Explore the consistent hashing implementation

---

## 📄 License

This project is open source and available for educational purposes.

---

**Built with ❤️ for learning distributed systems**
