# 🛡️ FraudGuard Console — Frontend

The **FraudGuard Console** is the frontend dashboard for the [Automated Fraud Detection & Alerting System](https://github.com/likkisamarthreddy/Automated-Fraud-Detection-and-Alerting-System-). It provides a real-time monitoring interface for detecting, analyzing, and managing fraudulent financial transactions.

---

## ✨ Features

| Module | Description |
|---|---|
| **Landing Page** | Modern animated landing page introducing the system |
| **Authentication** | Secure login with role-based access |
| **Dashboard** | Live overview of transaction volumes, risk scores, and fraud statistics |
| **Transactions** | Browse, search, and inspect individual transactions with risk details |
| **Alerts** | Real-time fraud alerts with severity levels and status management |
| **Analytics** | Charts and visualizations for fraud trends, patterns, and risk profiles |
| **Rule Management** | Create, edit, and toggle fraud detection rules |
| **Simulation** | Submit test transactions and observe fraud scoring in action |
| **System Health** | Monitor the health status of all backend microservices |

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | [React 18](https://react.dev/) + [TypeScript](https://www.typescriptlang.org/) |
| Build Tool | [Vite](https://vitejs.dev/) |
| Routing | [React Router v6](https://reactrouter.com/) |
| UI Components | [shadcn/ui](https://ui.shadcn.com/) + [Radix UI](https://www.radix-ui.com/) |
| Styling | [Tailwind CSS](https://tailwindcss.com/) |
| Animations | [Framer Motion](https://www.framer.com/motion/) |
| Charts | [Recharts](https://recharts.org/) |
| Data Fetching | [TanStack React Query](https://tanstack.com/query) + [Axios](https://axios-http.com/) |
| Forms | [React Hook Form](https://react-hook-form.com/) + [Zod](https://zod.dev/) |
| Testing | [Vitest](https://vitest.dev/) + [Testing Library](https://testing-library.com/) |

---

## 📁 Project Structure

```
fraudguard-console-main/
├── public/                  # Static assets
├── src/
│   ├── components/
│   │   ├── layout/          # DashboardLayout (sidebar + topbar)
│   │   ├── ui/              # 49 shadcn/ui components
│   │   └── NavLink.tsx      # Navigation link component
│   ├── pages/
│   │   ├── Landing.tsx      # Public landing page
│   │   ├── Login.tsx        # Authentication page
│   │   ├── Dashboard.tsx    # Main dashboard overview
│   │   ├── Transactions.tsx # Transaction list & details
│   │   ├── Alerts.tsx       # Fraud alert management
│   │   ├── Analytics.tsx    # Data visualizations
│   │   ├── RuleManagement.tsx # Fraud rule CRUD
│   │   ├── Simulation.tsx   # Transaction simulation
│   │   └── SystemHealth.tsx # Microservice health monitor
│   ├── services/
│   │   ├── authService.ts   # Authentication API calls
│   │   ├── transactionService.ts
│   │   ├── alertService.ts
│   │   └── ruleService.ts
│   ├── hooks/               # Custom React hooks
│   ├── lib/                 # Utility functions
│   ├── App.tsx              # Root component & routing
│   └── main.tsx             # Application entry point
├── index.html
├── tailwind.config.ts
├── vite.config.ts
└── package.json
```

---

## 🚀 Getting Started

### Prerequisites

- **Node.js** v18+ and **npm** — [install with nvm](https://github.com/nvm-sh/nvm#installing-and-updating)

### Installation

```bash
# Clone the repository
git clone https://github.com/likkisamarthreddy/Automated-Fraud-Detection-and-Alerting-System-.git

# Navigate to the frontend directory
cd Automated-Fraud-Detection-and-Alerting-System-/fraudguard-console-main

# Install dependencies
npm install

# Start the development server
npm run dev
```

The app will be available at **http://localhost:5173**

### Available Scripts

| Command | Description |
|---|---|
| `npm run dev` | Start dev server with hot reload |
| `npm run build` | Production build |
| `npm run preview` | Preview production build |
| `npm run lint` | Run ESLint |
| `npm run test` | Run tests with Vitest |
| `npm run test:watch` | Run tests in watch mode |

---

## 🔗 Backend Services

This frontend communicates with the following Spring Boot microservices (see [backend/](../backend/)):

| Service | Port | Purpose |
|---|---|---|
| **API Gateway** | `8080` | Routes requests to services |
| **Auth Service** | `8081` | JWT authentication & authorization |
| **Transaction Service** | `8082` | Transaction ingestion & storage |
| **Fraud Engine** | `8083` | Risk scoring & fraud analysis |
| **Alert Service** | `8084` | Fraud alert generation & management |

> Start the backend using `backend/run-backend-local.ps1` or via Docker Compose (`docker-compose.yml` at root).

---

## 📸 Pages Overview

- **`/`** — Landing page with system introduction and feature highlights
- **`/login`** — Secure login form
- **`/dashboard`** — Real-time fraud monitoring dashboard
- **`/transactions`** — Transaction list with risk scores
- **`/alerts`** — Active fraud alerts
- **`/analytics`** — Trend charts and fraud pattern analysis
- **`/rules`** — Fraud detection rule management
- **`/simulate`** — Test transaction simulation
- **`/system-health`** — Backend service health status

---

## 📄 License

This project is part of the [Automated Fraud Detection & Alerting System](https://github.com/likkisamarthreddy/Automated-Fraud-Detection-and-Alerting-System-).
