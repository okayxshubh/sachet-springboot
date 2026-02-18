# Architecture Overview
SACHET – HP Phase-1 is redesigned as a web-based system with a React.js frontend, Spring Boot backend, and PostgreSQL database. The architecture stays modular for future district/state scale-out and shared services.
Comment: Kept modular so the desktop-first flow can move to centralized deployments without major rewrites.

# High-Level Architecture Components
- React.js Web App (UI + Client Runtime)
- Java Spring Boot Backend Layer (Business Logic)
- PostgreSQL Database Layer (Relational Data Storage)
- Template Rendering Engine
- NCRP Processing Engine
- Notice Generation Engine
- Audit Logging Module
- AI Integration Service (Secure API Calls)
Comment: These components map 1:1 with the original Electron/Node/SQLite layers, just swapped for web + Spring + Postgres.

# Application Layer Breakdown
## 3.1 React.js Web App
Handles UI rendering, routing, state management, and secure API integration with Spring Boot.
Comment: React replaces the Electron desktop shell while keeping the same UI responsibilities.

## 3.2 Backend Business Logic Layer (Spring Boot)
Implements case management, NCRP parsing, notice grouping logic, diary management, evidence linking, chargesheet generation, and audit tracking.
Comment: Business rules remain centralized in the backend to keep the client thin.

## 3.3 Database Layer (PostgreSQL)
Stores structured relational data with enforced foreign keys, constraints, and transactional integrity.
Comment: PostgreSQL replaces SQLite and supports centralized scaling without schema redesign.

# Security Architecture
- Password hashing using bcrypt
- Role-based access control (SHO / IO)
- Audit logs for all critical actions
- Encrypted DB at rest (PostgreSQL disk/volume encryption)
- No cloud storage of investigation data
- Controlled AI API communication
Comment: Security posture remains local-first with strict access controls and full auditability.

# Data Flow Architecture
User Action → React UI → Spring Boot API → PostgreSQL → Optional AI Processing → Response Returned to UI.
Comment: Flow mirrors the original IPC pattern, now via REST APIs.

# Future Scalability Path
Phase-1 PostgreSQL can scale to centralized clusters or managed instances with minimal backend changes. React frontend remains reusable.
Comment: Database scale-up/down is now the main lever for capacity planning.

# Backup & Recovery Architecture
System provides scheduled encrypted database backups. Backup files stored in dedicated folder and can be restored via secure restore mechanism.
Comment: Use standard PostgreSQL backup tools with encryption at rest.

# Download Handling (Not via APIs)
Notice, chargesheet, and related document downloads are generated and delivered directly from React.js (client-side templates/PDF generation) to avoid dedicated download endpoints.
Comment: Keeps the API surface smaller and avoids heavy file streaming from the backend.
