# ⚡ Virtual Power Plant (VPP) System

## 📘 Overview

The **Virtual Power Plant (VPP)** system is a cloud-based platform designed to intelligently aggregate and manage distributed energy resources (batteries). It offers a REST API for managing batteries and retrieving energy data. Built with **Spring Boot**, the system is designed for scalability, maintainability, and testability.

---

## ✅ Features

### 🔋 Battery Management

- Add multiple batteries with:
  - `name`
  - `postcode`
  - `capacity` (watt-hours)
- Persist battery data to a **PostgreSQL** database using **Spring Data JPA**

### 🔍 Query by Postcode Range

- Retrieve battery names within a postcode range (sorted alphabetically)
- Return total and average watt capacity for the filtered results

### 🌟 Optional Enhancements

- Filter batteries by **minimum** and **maximum** capacity
- Support for **concurrent** battery registration
- Log key events with **SLF4J** and **Logback**

### 🧪 Testing

- Unit tests (70%+ coverage) with **JUnit 5** and **Mockito**
- Integration tests using **Testcontainers** for PostgreSQL

---

## 🛠️ Tech Stack

| Component     | Technology                  |
|---------------|-----------------------------|
| Language       | Java 21                     |
| Framework      | Spring Boot                 |
| ORM            | Spring Data JPA             |
| Database       | PostgreSQL                  |
| Testing        | JUnit 5, Testcontainers     |
| Build Tool     | Maven                       |
| Logging        | SLF4J + Logback + ELK Stack |
| Utilities      | Lombok                      |
| API Docs       | Swagger UI                  |

---

## 📦 Prerequisites

- Java 21 or higher (Java 21 recommended)
- Maven 3.8+
- Docker (for Testcontainers and containerization)
- PostgreSQL (optional if not using Docker)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/ponirsaha/powerledger-vpp.git
cd powerledger-vpp
````

### 2. Build and Run with Docker

```bash
docker-compose build vpp-app
docker-compose up
```

### 3. Access Swagger UI

Open your browser and navigate to:

```
http://localhost:8181/swagger-ui/index.html
```

---

## 📡 API Usage

### 🔧 Add Batteries

**Endpoint:** `POST /api/v1/batteries`

```bash
curl --location 'http://localhost:8181/api/v1/batteries' \
--header 'Content-Type: application/json' \
--data '[
  {
    "name": "Cannington",
    "postcode": "6107",
    "capacity": 13500
  },
  {
    "name": "Midland",
    "postcode": "6057",
    "capacity": 50500
  },
  {
    "name": "Hay Street",
    "postcode": "6000",
    "capacity": 23500
  },
  {
    "name": "Mount Adams",
    "postcode": "6525",
    "capacity": 12000
  },
  {
    "name": "Koolan Island",
    "postcode": "6733",
    "capacity": 10000
  },
  {
    "name": "Armadale",
    "postcode": "6992",
    "capacity": 25000
  },
  {
    "name": "Lesmurdie",
    "postcode": "6076",
    "capacity": 13500
  },
  {
    "name": "Kalamunda",
    "postcode": "6076",
    "capacity": 13500
  },
  {
    "name": "Carmel",
    "postcode": "6076",
    "capacity": 36000
  },
  {
    "name": "Bentley",
    "postcode": "6102",
    "capacity": 85000
  },
  {
    "name": "Akunda Bay",
    "postcode": "2084",
    "capacity": 13500
  },
  {
    "name": "Werrington County",
    "postcode": "2747",
    "capacity": 13500
  },
  {
    "name": "Bagot",
    "postcode": "0820",
    "capacity": 27000
  },
  {
    "name": "Yirrkala",
    "postcode": "0880",
    "capacity": 13500
  },
  {
    "name": "University of Melbourne",
    "postcode": "3010",
    "capacity": 85000
  },
  {
    "name": "Norfolk Island",
    "postcode": "2899",
    "capacity": 13500
  },
  {
    "name": "Ootha",
    "postcode": "2875",
    "capacity": 13500
  },
  {
    "name": "Kent Town",
    "postcode": "5067",
    "capacity": 13500
  },
  {
    "name": "Northgate Mc",
    "postcode": "9464",
    "capacity": 13500
  },
  {
    "name": "Gold Coast Mc",
    "postcode": "9729",
    "capacity": 50000
  }
]'
```

---

### 📊 Query Batteries by Postcode

**Endpoint:** `GET /api/v1/batteries?startPostcode=6000&endPostcode=7000`

```bash
curl --location 'http://localhost:8181/api/v1/batteries?startPostcode=6000&endPostcode=7000'
```

**Example Response:**

```json
{
  "batteries": [
    "Bentley",
    "Cannington",
    "Carmel",
    "Hay Street",
    "Kalamunda",
    "Lesmurdie",
    "Midland",
    "Mount Adams"
  ],
  "totalCapacity": 274500,
  "averageCapacity": 34312.5
}
```

---

## 🧪 Testing Strategy

| Type              | Description                            |
| ----------------- | -------------------------------------- |
| Unit Tests        | Validate core logic with **JUnit 5**   |
| Integration Tests | Use **Testcontainers** with PostgreSQL |
| Coverage Goal     | 70%+                                   |

---

