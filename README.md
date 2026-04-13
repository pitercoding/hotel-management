<h1 align="center">Hotel Management</h1>

<p align="center">
  <strong>Languages:</strong><br>
  <a href="README.pt.md">Portuguese</a> |
  <a href="README.md">English</a>
</p>

Hotel Management is a **full-stack hotel booking application** built with **Spring Boot**, **Angular**, and **MySQL**. It provides a role-based experience for **administrators** and **customers**, with JWT authentication, protected API routes, room management, and reservation workflows.

The system supports:

- User registration and login with JWT-based authentication
- Automatic role handling for `ADMIN` and `CUSTOMER`
- Admin room management with create, update, delete, and paginated listing
- Customer room browsing with availability filtering
- Reservation requests with date range selection and automatic total price calculation
- Admin reservation review flow with approval or rejection
- Swagger/OpenAPI documentation for the backend API

## 🎯 Project Motivation

This project was built to practice a realistic hotel management workflow with clear separation between backend business rules and frontend user flows.

The main goal was to implement a small but complete reservation platform that demonstrates:

- secure authentication with Spring Security and JWT
- role-based access to admin and customer features
- CRUD operations on hotel rooms
- reservation lifecycle handling from request to approval

## ✅ Current Features

### ⚙️ Backend (Spring Boot + MySQL)

- Authentication API:
  - `POST /api/auth/signup`
  - `POST /api/auth/login`
- Customer API:
  - `GET /api/customer/rooms/{pageNumber}`
  - `POST /api/customer/book`
  - `GET /api/customer/bookings/{userId}/{pageNumber}`
- Admin API:
  - `POST /api/admin/room`
  - `GET /api/admin/rooms/{pageNumber}`
  - `GET /api/admin/room/{id}`
  - `PUT /api/admin/room/{id}`
  - `DELETE /api/admin/room/{id}`
  - `GET /api/admin/reservations/{pageNumber}`
  - `PUT /api/admin/reservation/{reservationId}/{reservationStatus}`
- Security and infrastructure:
  - JWT authentication filter for protected endpoints
  - role-based authorization with `ADMIN` and `CUSTOMER`
  - stateless Spring Security configuration
  - CORS enabled for frontend/backend local integration
  - Swagger UI and OpenAPI configuration
  - actuator health/info exposure
- Business rules:
  - new users are created as `CUSTOMER`
  - a default admin account is created automatically on startup if none exists
  - customer room listing only returns rooms marked as available
  - reservations start with `PENDING` status
  - reservation total price is calculated from room price x number of days
  - approving or rejecting a reservation updates its status
  - approved reservations mark the room as unavailable
- Persistence and tests:
  - JPA entities for `User`, `Room`, and `Reservation`
  - repository, service, and controller tests
  - H2 in-memory configuration for backend tests

### 🖥️ Frontend (Angular + NG-ZORRO)

- Authentication screens:
  - registration form with validation
  - login form with JWT storage in `localStorage`
  - automatic redirect after login based on user role
- Admin area:
  - sidebar navigation for room and reservation management
  - room creation form
  - paginated dashboard listing rooms
  - room editing flow
  - room deletion with confirmation modal
  - reservations page with approve/reject actions
- Customer area:
  - available room listing with pagination
  - booking modal with check-in/check-out date range selection
  - personal bookings history with pagination
- UX behavior:
  - toast notifications for success, warning, and error feedback
  - route-level separation between auth, admin, and customer modules
  - token-aware menu rendering
  - responsive card/table-based pages using NG-ZORRO components

## 🔄 Application Flow

### 👤 Customer Flow

1. Register a new account
2. Login as customer
3. Browse available rooms
4. Choose a room and submit a booking request with a date range
5. Check booking history and current reservation status

### 🛠️ Admin Flow

1. Login with an admin account
2. Create, update, and remove rooms
3. View paginated reservation requests
4. Approve or reject reservations

## 🧰 Technologies

### ⚙️ Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- MySQL
- JWT (`jjwt`)
- Springdoc OpenAPI / Swagger UI
- Maven
- JUnit 5 + H2

### 🖥️ Frontend

- Angular 21
- TypeScript
- Angular Router
- Reactive Forms
- RxJS
- NG-ZORRO Ant Design
- SCSS

### 🚀 DevOps / Tooling

- Docker
- Docker Compose

## ▶️ How to Run Locally

### 1. 📥 Clone the repository

```bash
git clone https://github.com/pitercoding/hotel-management.git
cd hotel-management
```

### 2. 🔐 Configure environment variables

Create a `.env` file in the project root with values like:

```bash
DB_USER=your_mysql_user
DB_PASS=your_mysql_password
DB_NAME=hotel_db
DB_ROOT_PASS=your_mysql_root_password
JWT_SECRET=your_jwt_secret
```

### 3. 🗄️ Start MySQL

You can use your local MySQL instance or start only the database with Docker:

```bash
docker compose up -d mysql
```

### 4. ⚙️ Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

On Windows:

```bash
cd backend
mvnw.cmd spring-boot:run
```

### 5. 🖥️ Run the frontend

```bash
cd frontend
npm install
npm start
```

### 6. 🌐 Open the application

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## 🐳 Run With Docker Compose

To start the full stack with containers:

```bash
docker compose up --build
```

This starts:

- MySQL on `3306`
- Spring Boot backend on `8080`
- Angular frontend on `4200`

## 👑 Default Admin Account

The backend creates a default admin user automatically if no admin exists:

- Email: `admin@test.com`
- Password: `admin`

This is intended for development/demo use and should be changed in a production-ready setup.

## 🔌 API Notes

- `/api/auth/**` is public
- `/api/admin/**` requires the `ADMIN` role
- `/api/customer/**` requires the `CUSTOMER` role
- The frontend stores the JWT token and user role in `localStorage`
- Room lists are paginated
- Customer room search returns only rooms with `available = true`
- Booking requests are created as `PENDING`
- Reservation status values handled by the backend are `APPROVED` and `REJECTED`

## 🧪 Testing Status

Current status:

- backend repository tests are implemented
- backend service tests are implemented
- backend controller tests are implemented
- frontend component/service spec files are present
- backend tests use H2 in-memory database configuration

Recommended next test scope:

- add frontend route/auth guard tests
- add frontend integration tests for login and booking flows
- add more validation and edge-case coverage for reservation dates
- add end-to-end tests for admin and customer journeys

## 🔮 Next Improvements

### 📦 Product

- Add room photos upload/storage instead of static image usage
- Add booking cancellation flow
- Add availability conflict validation by date range
- Add search and filters by room type and price

### 🔐 Security and Backend

- Move secrets to a dedicated environment strategy for deployment
- Add refresh token or token expiration/renewal flow
- Improve validation and standardized error responses
- Add database migrations with Flyway or Liquibase

### 🎨 Frontend and UX

- Add route guards for admin/customer modules
- Improve empty states and loading states
- Add dashboard metrics for reservations and occupancy
- Improve mobile navigation behavior

### 🚚 Delivery

- Add CI pipeline for lint, test, and build
- Add production deployment configuration

## 📁 Folder Structure

```text
hotel-management/
|-- backend/                                 # Spring Boot API
|   |-- src/main/java/com/pitercoding/backend/
|   |   |-- configs/                         # Security, JWT, CORS, OpenAPI
|   |   |-- controller/
|   |   |   |-- admin/                      # Admin room and reservation endpoints
|   |   |   |-- auth/                       # Signup and login endpoints
|   |   |   `-- customer/                   # Customer room and booking endpoints
|   |   |-- dto/                            # Request and response DTOs
|   |   |-- entity/                         # JPA entities
|   |   |-- enums/                          # Domain enums
|   |   |-- repository/                     # Spring Data repositories
|   |   |-- services/                       # Business logic by domain
|   |   |-- util/                           # JWT utility
|   |   `-- BackendApplication.java         # Spring Boot entrypoint
|   |-- src/main/resources/
|   |   `-- application.properties          # Main backend configuration
|   |-- src/test/                           # Backend unit/integration tests
|   |-- Dockerfile
|   `-- pom.xml
|-- frontend/                                # Angular application
|   |-- src/app/
|   |   |-- auth/                           # Login, register, auth storage/services
|   |   |-- modules/
|   |   |   |-- admin/                      # Admin pages, services, routing
|   |   |   `-- customer/                   # Customer pages, services, routing
|   |   |-- app.config.ts                   # Global Angular providers
|   |   |-- app.routes.ts                   # Root routes
|   |   |-- app.html                        # Main shell and sidebar layout
|   |   `-- DemoNgZorroAntdModule.ts        # NG-ZORRO shared imports
|   |-- public/images/                      # Static brand and UI images
|   |-- angular.json
|   |-- Dockerfile
|   `-- package.json
|-- docker-compose.yml                       # Multi-container local setup
|-- README.md                                # Documentation (English)
`-- README.pt.md                             # Documentation (Portuguese)
```

## 🖼️ Screenshots & Visuals

### 🔐 Login Page

![Login Page](frontend/public/screenshots/login_page.png)

### 📝 Register Page

![Register Page](frontend/public/screenshots/register_page.png)

### 🏨 Admin Room View

![Admin Room View](frontend/public/screenshots/admin_room_view.png)

### ➕ Admin Post Room

![Admin Post Room](frontend/public/screenshots/admin_post_room.png)

### 📋 Admin Reservations

![Admin Reservations](frontend/public/screenshots/admin_reservations.png)

### 🛏️ Customer Rooms

![Customer Rooms](frontend/public/screenshots/customer_rooms.png)

### 📖 Customer Bookings

![Customer Bookings](frontend/public/screenshots/customer_bookings.png)

## 📄 License

This project is licensed under the **MIT License**.

## 👤 Author

**Piter Gomes** - Computer Science Student (6th Semester) & Full-Stack Developer

[Email](mailto:piterg.bio@gmail.com) | [LinkedIn](https://www.linkedin.com/in/piter-gomes-4a39281a1/) | [GitHub](https://github.com/pitercoding) | [Portfolio](https://portfolio-pitergomes.vercel.app/)
