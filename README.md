# Finance Data Processing and Access Control Backend

A comprehensive Spring Boot backend application for managing financial records with role-based access control, JWT authentication, and dashboard analytics.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security 6** with JWT authentication (jjwt 0.11.5)
- **Spring Data JPA** for persistence
- **MySQL 8** database
- **Bean Validation** (Jakarta Validation)
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Maven** for build and dependency management
- **Lombok** to reduce boilerplate code

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

## Setup Instructions

### 1. Database Setup

Create a MySQL database named `finance_db`:

```sql
CREATE DATABASE finance_db;
```

### 2. Configure Database Credentials

Update the `src/main/resources/application.properties` file with your MySQL credentials:

```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build the Application

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Default Seed Users

The application automatically creates three users on first startup (when the users table is empty):

| Email | Password | Role |
|-------|----------|------|
| admin@finance.com | admin123 | ADMIN |
| analyst@finance.com | analyst123 | ANALYST |
| viewer@finance.com | viewer123 | VIEWER |

All seed users have `ACTIVE` status.

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and receive JWT token | No |

### Users (Admin Only)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | Yes (Admin) |
| GET | `/api/users/{id}` | Get user by ID | Yes (Admin) |
| PUT | `/api/users/{id}/role` | Update user role | Yes (Admin) |
| PUT | `/api/users/{id}/status` | Update user status | Yes (Admin) |

### Financial Records

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/records` | Create a new record | Yes (Admin/Analyst) |
| GET | `/api/records` | Get all records (with optional filters) | Yes (All Roles) |
| GET | `/api/records/{id}` | Get record by ID | Yes (All Roles) |
| PUT | `/api/records/{id}` | Update a record | Yes (Admin/Analyst) |
| DELETE | `/api/records/{id}` | Soft delete a record | Yes (Admin) |

**Query Parameters for GET /api/records:**

- `type` - Filter by transaction type (INCOME or EXPENSE)
- `category` - Filter by category (e.g., Salary, Rent, Food)
- `startDate` - Filter from date (format: yyyy-MM-dd)
- `endDate` - Filter to date (format: yyyy-MM-dd)
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)

### Dashboard (Admin/Analyst Only)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/dashboard/summary` | Get summary totals | Yes (Admin/Analyst) |
| GET | `/api/dashboard/by-category` | Get totals grouped by category | Yes (Admin/Analyst) |
| GET | `/api/dashboard/monthly-trends` | Get monthly income/expense trends | Yes (Admin/Analyst) |
| GET | `/api/dashboard/recent` | Get recent transactions (top 10) | Yes (Admin/Analyst) |

### API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

## Sample API Requests

### Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@finance.com",
    "password": "admin123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "admin@finance.com",
  "role": "ADMIN",
  "name": "Admin User"
}
```

### Create a Financial Record

```bash
curl -X POST http://localhost:8080/api/records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 5000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2025-04-01",
    "notes": "Monthly salary"
  }'
```

### Get Dashboard Summary

```bash
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Records with Filters

```bash
curl -X GET "http://localhost:8080/api/records?type=INCOME&category=Salary&startDate=2025-01-01&endDate=2025-03-31&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Role Permission Matrix

| Action | VIEWER | ANALYST | ADMIN |
|--------|--------|---------|-------|
| Read all records | ✓ | ✓ | ✓ |
| Create records | ✗ | ✓ | ✓ |
| Update records | ✗ | ✓ | ✓ |
| Delete records | ✗ | ✗ | ✓ |
| View dashboard | ✗ | ✓ | ✓ |
| Manage users | ✗ | ✗ | ✓ |
| View all users | ✗ | ✗ | ✓ |
| Update user roles | ✗ | ✗ | ✓ |
| Update user status | ✗ | ✗ | ✓ |

## Data Models

### User

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| name | String | User name |
| email | String | Unique email address |
| password | String | BCrypt hashed password |
| role | Enum | VIEWER, ANALYST, or ADMIN |
| status | Enum | ACTIVE or INACTIVE |
| createdAt | LocalDateTime | Creation timestamp |
| updatedAt | LocalDateTime | Last update timestamp |

### FinancialRecord

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| amount | BigDecimal | Amount with 15,2 precision |
| type | Enum | INCOME or EXPENSE |
| category | String | Category name |
| date | LocalDate | Transaction date |
| notes | String | Optional notes (TEXT) |
| isDeleted | Boolean | Soft delete flag |
| createdBy | User | Reference to creating user |
| createdAt | LocalDateTime | Creation timestamp |
| updatedAt | LocalDateTime | Last update timestamp |

## Assumptions Made

1. **JWT Secret**: The JWT secret is stored in application.properties. In production, this should be stored in a secure vault or as an environment variable.
2. **Soft Deletes**: Records are soft-deleted (isDeleted flag) rather than physically removed.
3. **Audit Trails**: createdAt and updatedAt timestamps are automatically managed via JPA lifecycle callbacks.
4. **Default Role**: New registrations default to VIEWER role.
5. **Pagination**: Default page size is 10 records; max not enforced but should be set at application level.
6. **Currency**: All amounts are stored as BigDecimal with 2 decimal places, assumed to be in a single currency (no multi-currency support).
7. **Date Format**: Dates use ISO format (yyyy-MM-dd).
8. **User Filtering**: All queries filter on isDeleted = false to exclude soft-deleted records.
9. **Monthly Trends**: Limited to the last 12 months of data.
10. **Database**: MySQL 8 is used with Hibernate's auto-update DDL mode for development convenience.

## Design Decisions and Tradeoffs

### Security

- **JWT Stateless Authentication**: Chosen for scalability and simplicity in a RESTful API. Tradeoff: Token revocation requires additional infrastructure (e.g., token blacklist).
- **BCrypt**: Used for password hashing as it's the industry standard with configurable work factor.
- **CSRF Disabled**: Appropriate for stateless JWT-based APIs, but would need reconsideration for session-based auth.

### Data Access

- **Spring Data JPA**: Simplifies repository implementations with derived queries and custom @Query methods.
- **Custom Queries**: Used for dashboard aggregations to optimize performance and reduce N+1 queries.
- **Lazy Loading**: ManyToOne relationships use LAZY fetch to avoid unnecessary joins.

### API Design

- **RESTful**: Follows REST conventions with proper HTTP verbs and status codes.
- **Pagination**: Implemented on the records endpoint to handle large datasets efficiently.
- **Filtering**: Multiple optional filter parameters with IS NULL OR pattern for flexibility.
- **OpenAPI/Swagger**: Auto-generated documentation for better developer experience.

### Error Handling

- **GlobalExceptionHandler**: Centralized exception handling with consistent error response format.
- **Specific Exceptions**: Custom exceptions for domain errors (ResourceNotFound, Conflict, BadRequest).
- **Validation**: Bean Validation on DTOs with detailed field-level error responses.

### Code Quality

- **Lombok**: Reduces boilerplate code for getters, setters, builders, etc.
- **Logging**: SLF4J with Logback for structured logging.
- **DTOs**: Separate request/response objects to decouple API from internal models.
- **Service Layer**: Separation of concerns with services handling business logic.
- **Method Security**: Uses @PreAuthorize for role-based access control at method level.

### Scalability Considerations

- **Current**: Single database, monolithic architecture – suitable for small to medium workloads.
- **Future**: Could be horizontally scaled behind a load balancer; JWT enables stateless scaling.
- **Potential Bottlenecks**: Complex aggregations and large dataset queries may need optimization with proper indexing.

## Project Structure

```
src/main/java/com/finance/backend/
├── DataApplication.java          # Main Spring Boot application
├── config/
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   └── DataLoader.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── RecordController.java
│   └── DashboardController.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── RecordService.java
│   └── DashboardService.java
├── repository/
│   ├── UserRepository.java
│   └── RecordRepository.java
├── model/
│   ├── User.java
│   └── FinancialRecord.java
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── UserResponse.java
│   ├── UpdateRoleRequest.java
│   ├── UpdateStatusRequest.java
│   ├── RecordRequest.java
│   ├── RecordResponse.java
│   ├── PagedResponse.java
│   ├── DashboardSummary.java
│   ├── CategoryTotal.java
│   ├── MonthlyTrend.java
│   └── ErrorResponse.java
├── enums/
│   ├── Role.java
│   ├── Status.java
│   └── TransactionType.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── CustomUserDetailsService.java
└── exception/
    ├── ResourceNotFoundException.java
    ├── ConflictException.java
    ├── BadRequestException.java
    └── GlobalExceptionHandler.java
```

## Testing with Swagger UI

Once the application is running:

1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Click the "Authorize" button
3. Enter: `Bearer YOUR_JWT_TOKEN` (obtain via login endpoint)
4. All protected endpoints will now be available to test

## Notes

- All financial amounts are stored with 2 decimal places precision (scale=2) and total precision of 15 digits.
- The application uses soft deletes for financial records to maintain audit trail.
- All timestamps are automatically managed by JPA lifecycle callbacks.
- The `DataLoader` only seeds data if the users table is empty, ensuring idempotency.

## Future Enhancements

- Email verification for registration
- Password reset functionality
- Refresh tokens for better security
- Multi-currency support
- Export reports (PDF, Excel)
- Advanced filtering with date ranges
- Audit trail for all user actions
- Rate limiting
- API versioning

---

**Built with Spring Boot 3.2.x, Java 17, and MySQL 8**
