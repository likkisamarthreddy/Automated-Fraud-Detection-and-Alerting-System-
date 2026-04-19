---
description: How to run the Fraud Detection System (Frontend + Backend)
---

# How to Run the Application

You can run the application in two ways: **Fully Dockerized** (easiest) or **Hybrid Local** (best for development).

## Option 1: Fully Dockerized (Recommended for Demo)

Run the entire system (Database, Kafka, Backend, Frontend) with a single command.

1.  **Start all services:**
    ```powershell
    docker-compose up -d
    ```
2.  **Access the application:**
    -   Frontend: [http://localhost:3001](http://localhost:3001)
    -   API Gateway: [http://localhost:8080](http://localhost:8080)

---

## Option 2: Hybrid Local (Recommended for Development)

Run infrastructure in Docker, but run Backend and Frontend code locally for debugging.

### Step 1: Start Infrastructure
Start only the required databases and message brokers:
```powershell
docker-compose up -d mysql redis kafka zookeeper
```

### Step 2: Start Backend (Java Spring Boot)
Use the provided PowerShell script to build and run all microservices:
```powershell
# In the root directory
./backend/run-backend-local.ps1
```
*Note: This script assumes you have Maven installed and accessible.*

### Step 3: Start Frontend (React)
1.  Open a new terminal.
2.  Navigate to the frontend directory:
    ```powershell
    cd fraudguard-console-main
    ```
3.  Install dependencies (first time only):
    ```powershell
    npm install
    ```
4.  Start the development server:
    ```powershell
    npm run dev
    ```
5.  Access the frontend at [http://localhost:3000](http://localhost:3000)

---

## Troubleshooting

-   **Port Conflicts:** Ensure ports 3306/3307 (MySQL), 6379 (Redis), 8080-8084 (Backend), and 3000/3001 (Frontend) are free.
-   **Maven:** If `run-backend-local.ps1` fails, check if `mvn -version` works in your terminal.
