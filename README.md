# 🔗 TinyURL Service

A distributed **URL Shortener** built with **Spring Boot**, **MongoDB**, **Redis**, and **Zookeeper**, packaged with Docker for easy setup.  
This project also demonstrates **consistent hashing** and load balancer simulation.

Inspired from https://systemdesign.one/url-shortening-system-design/#further-system-design-learning-resources

---

## 📸 Architecture
![tinyurl](https://github.com/user-attachments/assets/7f977690-aa4c-49fa-ace0-013e3bd0dc39)

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
curl --location 'http://localhost:9001/api/v1/shorten' --header 'Content-Type: application/json' --data '{
    "longUrl":"https://example.com"
}'
```

✅ Response:
```json
{
  "shortUrl": "http://localhost:9001/api/v1/4j0",
  "longUrl": "https://example.com"
}
```

---

#### 2. Expand a Short URL
```bash
curl --location --request GET 'http://localhost:9001/api/v1/4j0'
```

✅ Response:
```json
{
  "longUrl": "https://example.com"
}
```

---

## ⚙️ Project Highlights
- Spring Boot backend with modular design
- MongoDB for URL persistence
- Redis for fast lookups & caching
- Zookeeper for coordination
- Dockerized microservices with multi-instance support
- Consistent Hashing based Load Balancer → https://github.com/VaibhavKVerma/ConsistentHashing

---

## 🧪 Load Testing
To simulate parallel requests across services:

```bash
seq 1 1000 | xargs -n 1 -P 100 -I {} curl --location 'http://localhost:9001/api/v1/shorten'   --header 'Content-Type: application/json'   --data '{"longUrl":"https://example.com"}' &

seq 1 1000 | xargs -n 1 -P 100 -I {} curl --location 'http://localhost:9002/api/v1/shorten'   --header 'Content-Type: application/json'   --data '{"longUrl":"https://example.com"}' &
wait
```

---

## 📚 Related Work
Check out my Consistent Hashing Simulation Repo: https://github.com/VaibhavKVerma/ConsistentHashing
Check out my Bloom Filter Repo: https://github.com/VaibhavKVerma/BloomFilter

---

## 🎉 Voilà!
Your TinyURL service is up and running 🚀
