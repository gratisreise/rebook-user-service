# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot microservice (rebook-user-service) that handles user management, authentication, and profile operations for the Rebook platform. The service is part of a larger microservices architecture using Spring Cloud with Eureka for service discovery and Spring Cloud Config for centralized configuration.

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Build Docker image
docker build -t nooaahh/rebook-user-service:latest .

# Push Docker image (multi-platform)
docker buildx build --platform=linux/amd64 -t nooaahh/rebook-user-service --push .

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# View coverage report (generated in build/reports/jacoco/test/html/index.html)
open build/reports/jacoco/test/html/index.html
```

### Code Quality
```bash
# Clean build
./gradlew clean build

# Check dependencies
./gradlew dependencies

# View test results
./gradlew test --info
```

## High-Level Architecture

### Microservices Integration
- **Service Discovery**: Uses Netflix Eureka (`@EnableDiscoveryClient`)
- **Configuration Management**: Spring Cloud Config client connecting to rebook-config service
- **Inter-Service Communication**: OpenFeign clients (`@EnableFeignClients`)
- **API Documentation**: Swagger UI available at `/swagger-ui.html`
- **Data Auditing**: JPA Auditing enabled (`@EnableJpaAuditing`)

### Authentication Architecture
The service implements a dual-token authentication system:

1. **External Authentication**: Keycloak integration for OAuth2/OpenID Connect
   - Users authenticate via Keycloak and receive external access tokens
   - `KeycloakJwtUtil` validates and extracts user information from Keycloak tokens
   - Keycloak Admin Client integration for user management

2. **Internal JWT System**: Custom JWT tokens for service-to-service communication
   - `JwtUtil` creates internal access/refresh tokens after Keycloak validation
   - Redis-based refresh token caching with `refresh:` prefix
   - New users are automatically created with default nickname and profile image
   - Automatic notification settings creation via NotificationClient

### Core Components

#### Controllers
- **AuthController** (`/api/auths`): Login, token refresh, test endpoints
  - `POST /login` - Keycloak token로 로그인 및 내부 JWT 발급
  - `POST /refresh` - Access Token 갱신
  - `GET /test` - 헬스 체크

- **UsersController** (`/api/users`): User CRUD operations, profile management
  - `GET /` - 내 프로필 조회
  - `GET /{userId}` - 특정 사용자 조회
  - `PUT /` - 프로필 수정 (이미지 포함)
  - `DELETE /` - 계정 삭제
  - `PATCH /me` - 비밀번호 변경
  - `GET /categories` - 내 선호 카테고리 조회
  - `GET /categories/recommendations/{userId}` - 추천 카테고리 조회

- **FavoriteCategoryController** (`/api/categories`): User favorite category management
  - `POST /` - 선호 카테고리 추가
  - `DELETE /{categoryId}` - 선호 카테고리 삭제
  - `GET /` - 내 선호 카테고리 목록 조회

- **ReaderController** (`/api/readers`): User reading activities
  - `GET /books` - 내 도서 상호작용 목록
  - `GET /tradings` - 내 거래 이력

#### Services & Business Logic
- **AuthService**: Handles login flow, token management, user creation
  - Keycloak 토큰 검증 후 사용자 정보 추출
  - 신규 사용자 자동 생성 (기본 닉네임 + 프로필 이미지)
  - 내부 JWT 토큰 생성 및 Redis 캐싱
  - NotificationClient 연동하여 알림 설정 자동 생성

- **UsersService**: User profile operations, updates, retrieval
- **KeycloakService**: Integration with Keycloak identity provider
- **S3Service**: File upload/download for profile images using AWS S3
- **RedisService**: Caching layer for session management and performance
- **FavoriteCategoryService**: User favorite category CRUD operations
- **UserReader**: User data read operations (separation of concerns)
- **FavoriteCategoryReader**: Favorite category read operations

#### Data Layer
- **JPA Entities**:
  - `Users` - Main user entity with Keycloak integration
    - Fields: id (Keycloak ID), username, email, nickname, profileImage, role (enum), createdAt, updatedAt
    - JPA Auditing enabled for automatic timestamp management
    - Role enum: USER, ADMIN

  - `FavoriteCategory` - User favorite categories with composite key
    - Composite key: userId + categoryId
    - Join table for many-to-many relationship

- **PostgreSQL**: Primary database for user data persistence
- **Redis**: Session storage and caching layer (refresh tokens)
- **Composite Keys**: FavoriteCategoryId for many-to-many relationships

#### External Integrations
- **NotificationClient** (OpenFeign): Feign client for notification-service integration
  - `POST /api/notifications/me/settings/{userId}` - Create notification settings

### Configuration Profiles
- **dev**: Development environment, connects to rebook-config:8888
- **prod**: Production environment configuration
- **Default**: Falls back to dev profile

### External Dependencies
- **Keycloak**: User authentication and identity management
  - OAuth2/OIDC authentication provider
  - Admin client for user management
  - Token validation and user info extraction

- **AWS S3**: Profile image storage and management
  - S3 client configuration via S3Config
  - Basic profile image URL configurable via `aws.basic` property

- **PostgreSQL**: User data persistence
- **Redis**: Session management and caching
- **RabbitMQ**: Message queue integration for event-driven communication
- **Prometheus**: Metrics collection via Micrometer
- **Sentry**: Error tracking and monitoring

### Exception Handling
Centralized exception handling via `GlobalExceptionHandler` with custom exceptions:
- `CMissingDataException`: Missing or invalid data (404)
- `CDuplicatedDataException`: Duplicate data conflicts (409)
- `CInvalidDataException`: Data validation failures (400)

### Response Structure
Standardized API responses using:
- `SingleResult<T>`: Single object responses
- `ListResult<T>`: Collection responses
- `CommonResult`: Base response with status and message
- `ResponseService`: Factory for creating standardized responses
- `ResultCode`: Response code constants

## Key Implementation Notes

### User Creation Flow
1. User authenticates via Keycloak (external system)
2. Service validates Keycloak token and extracts user information via `KeycloakJwtUtil`
3. If user doesn't exist locally (`userRepository.existsById(userId)` returns false):
   - Creates new Users entity with Keycloak UserInfo
   - Sets default nickname: "닉네임" + userId
   - Sets default profile image from `aws.basic` property
   - Saves to database
   - Calls NotificationClient to create notification settings
4. Generates internal JWT tokens (access + refresh) via `JwtUtil`
5. Caches refresh token in Redis with `refresh:` prefix
6. Returns TokenResponse with both tokens

### Authentication Flow Details
```
Client Request with Keycloak Token
         ↓
KeycloakJwtUtil validates token
         ↓
Extract UserInfo (userId, username, email, role)
         ↓
Check if user exists in local DB
         ↓
[New User] → Create Users entity → Save to DB → Create notification settings
[Existing User] → Continue
         ↓
JwtUtil.createAccessToken(userId)
JwtUtil.createRefreshToken(userId)
         ↓
RedisService.set("refresh:" + refreshToken, "true")
         ↓
Return TokenResponse(accessToken, refreshToken)
```

### Token Refresh Flow
```
Client sends refresh token
         ↓
Check Redis: redisService.get("refresh:" + refreshToken)
         ↓
[Not Found] → throw CMissingDataException("Invalid refresh token")
[Found] → Continue
         ↓
Extract userId from refresh token via JwtUtil.getUserId()
         ↓
Generate new access token: JwtUtil.createAccessToken(userId)
         ↓
Return RefreshResponse(new accessToken)
```

### Database Relationships
- Users entity uses Keycloak ID as primary key (String type)
- FavoriteCategory uses composite key (userId + categoryId)
- JPA Auditing automatically manages createdAt and updatedAt timestamps
- Role is stored as enum (STRING type in DB): USER, ADMIN

### JWT Token Management
- Access tokens: Short-lived, used for API authentication
- Refresh tokens: Cached in Redis with `refresh:` prefix for renewal
- Token validation includes user ID extraction and Redis cache verification
- Both tokens created and validated by `JwtUtil`

### Service Communication Patterns
- **OpenFeign**: Synchronous HTTP calls to other microservices
  - NotificationClient for notification-service integration
  - Service discovery via Eureka (service name: "notification-service")

- **Service Discovery**: All services register with Eureka
  - Dynamic service resolution by service name
  - Load balancing handled by Spring Cloud LoadBalancer

### File Upload Strategy
- Profile images uploaded to AWS S3
- S3 client configured via S3Config
- S3Service handles upload/download operations
- Default image URL provided via application configuration

### Caching Strategy
- Redis used for refresh token storage
- Key pattern: `refresh:{refreshToken}` → `"true"`
- RedisService provides abstraction for cache operations
- TTL and expiration managed by Redis configuration

## Project Structure Details

```
src/main/java/com/example/rebookuserservice/
├── RebookUserServiceApplication.java        # Main application entry point
│   @SpringBootApplication
│   @EnableDiscoveryClient                   # Eureka service registration
│   @EnableFeignClients                      # OpenFeign client support
│   @EnableJpaAuditing                       # Automatic timestamp management
│
├── advice/                                  # Global exception handling
│   └── GlobalExceptionHandler.java          # @RestControllerAdvice for unified error responses
│
├── clients/                                 # Feign clients for inter-service communication
│   └── NotificationClient.java              # @FeignClient for notification-service
│
├── common/                                  # Standardized response models
│   ├── CommonResult.java                    # Base response structure
│   ├── SingleResult.java                    # Single data response wrapper
│   ├── ListResult.java                      # Collection response wrapper
│   ├── ResponseService.java                 # Response factory methods
│   └── ResultCode.java                      # Response code constants
│
├── config/                                  # Infrastructure configuration
│   ├── RedisConfig.java                     # Redis connection and template config
│   ├── S3Config.java                        # AWS S3 client configuration
│   ├── KeycloakConfig.java                  # Keycloak integration settings
│   └── SwaggerConfig.java                   # OpenAPI documentation config
│
├── controller/                              # REST API endpoints
│   ├── AuthController.java                  # /api/auths - Login, refresh, test
│   ├── UsersController.java                 # /api/users - User CRUD and profile
│   ├── FavoriteCategoryController.java      # /api/categories - Favorite categories
│   └── ReaderController.java                # /api/readers - Reading activities
│
├── enums/                                   # Enumeration types
│   └── Role.java                            # User roles (USER, ADMIN)
│
├── exception/                               # Custom exceptions
│   ├── CMissingDataException.java           # 404 - Data not found
│   ├── CDuplicatedDataException.java        # 409 - Duplicate data
│   └── CInvalidDataException.java           # 400 - Invalid input
│
├── model/                                   # Data transfer objects and entities
│   ├── entity/                              # JPA entities
│   │   ├── Users.java                       # Main user entity (Keycloak integration)
│   │   ├── FavoriteCategory.java            # User favorite categories
│   │   └── compositekey/
│   │       └── FavoriteCategoryId.java      # Composite primary key for FavoriteCategory
│   │
│   ├── feigns/                              # DTOs for Feign clients
│   │   └── AuthorsRequest.java              # Request DTO for external service calls
│   │
│   └── [Request/Response DTOs]              # API request and response models
│       ├── LoginRequest.java                # Login request with Keycloak token
│       ├── TokenResponse.java               # JWT tokens response
│       ├── RefreshRequest.java              # Refresh token request
│       ├── RefreshResponse.java             # New access token response
│       ├── UsersResponse.java               # User profile response
│       ├── UsersUpdateRequest.java          # Profile update request
│       ├── PasswordUpdateRequest.java       # Password change request
│       ├── CategoryRequest.java             # Category operation request
│       ├── CategoryResponse.java            # Category data response
│       ├── UserInfo.java                    # Keycloak user information
│       ├── KeycloakRequest.java             # Keycloak API request
│       ├── KeycloakResponse.java            # Keycloak API response
│       └── BookInfo.java                    # Book information DTO
│
├── repository/                              # Data access layer
│   ├── UserRepository.java                  # JpaRepository<Users, String>
│   └── FavoriteCategoryRepository.java      # JpaRepository<FavoriteCategory, FavoriteCategoryId>
│
├── service/                                 # Business logic layer
│   ├── AuthService.java                     # Authentication and token management
│   ├── UsersService.java                    # User CRUD operations
│   ├── KeycloakService.java                 # Keycloak integration service
│   ├── S3Service.java                       # AWS S3 file operations
│   ├── RedisService.java                    # Redis caching operations
│   ├── FavoriteCategoryService.java         # Favorite category operations
│   ├── UserReader.java                      # User read-only operations
│   └── FavoriteCategoryReader.java          # Category read-only operations
│
└── utils/                                   # Utility classes
    ├── JwtUtil.java                         # Internal JWT creation and validation
    └── KeycloakJwtUtil.java                 # Keycloak token validation and parsing
```

### Configuration Files
```
src/main/resources/
├── application.yaml                         # Base configuration
│   - spring.application.name: user-service
│   - spring.profiles.active: dev
│
├── application-dev.yaml                     # Development environment config
│   - External config server connection
│   - Local service ports and database
│
└── application-prod.yaml                    # Production environment config
    - Production-grade settings
    - External service endpoints
```

### Build Configuration
```
build.gradle                                 # Gradle build configuration
├── Spring Boot 3.3.13
├── Java 17
├── Spring Cloud 2023.0.5
├── Dependencies:
│   ├── spring-boot-starter-web              # REST API
│   ├── spring-boot-starter-data-jpa         # Database ORM
│   ├── spring-boot-starter-data-redis       # Redis caching
│   ├── spring-boot-starter-amqp             # RabbitMQ messaging
│   ├── spring-boot-starter-actuator         # Monitoring
│   ├── spring-boot-starter-validation       # Bean validation
│   ├── spring-cloud-starter-config          # Config client
│   ├── spring-cloud-starter-netflix-eureka-client  # Service discovery
│   ├── spring-cloud-starter-openfeign       # HTTP client
│   ├── keycloak-admin-client:26.0.5         # Keycloak integration
│   ├── io.jsonwebtoken:jjwt-api:0.12.5      # JWT handling
│   ├── software.amazon.awssdk:s3            # AWS S3
│   ├── springdoc-openapi-starter-webmvc-ui:2.6.0  # Swagger
│   ├── io.sentry:sentry-spring-boot-starter-jakarta  # Error tracking
│   ├── io.micrometer:micrometer-registry-prometheus  # Metrics
│   └── postgresql, lombok, devtools
│
└── Plugins:
    ├── org.springframework.boot
    ├── io.spring.dependency-management
    └── jacoco (test coverage)
```

### Docker Configuration
```
Dockerfile                                   # Multi-stage Docker build
├── Stage 1: Builder
│   - Base: gradle:8.14.2-jdk17
│   - Download dependencies (cached layer)
│   - Build JAR with bootJar
│
└── Stage 2: Runtime
    - Base: openjdk:17-slim
    - Copy JAR from builder
    - Expose port 8080
    - Entry point: java -jar app.jar
```

## Important Implementation Details

### Keycloak Integration
- **Token Validation**: KeycloakJwtUtil parses and validates Keycloak JWT tokens
- **User Information Extraction**: Extracts userId, username, email, role from token claims
- **Admin Client**: Keycloak Admin Client configured for user management operations
- **Auto User Creation**: First-time users automatically created with Keycloak info

### Redis Caching Pattern
- **Refresh Token Storage**: Key pattern `refresh:{token}` with value `"true"`
- **Token Validation**: Check existence in Redis before allowing refresh
- **Session Management**: Redis serves as distributed session store
- **TTL Management**: Token expiration handled by Redis configuration

### Security Considerations
- **Dual Authentication**: External Keycloak + Internal JWT ensures secure service communication
- **Token Separation**: External tokens never stored, only internal tokens cached
- **Role-Based Access**: Role enum (USER, ADMIN) for authorization
- **Stateless Architecture**: JWT-based authentication enables horizontal scaling

### Microservice Communication
- **Service Discovery**: Eureka enables dynamic service location
- **Load Balancing**: Client-side load balancing via Spring Cloud LoadBalancer
- **Fault Tolerance**: Feign clients support retry and fallback mechanisms
- **Async Messaging**: RabbitMQ for event-driven communication (notification events)

### Data Consistency
- **JPA Auditing**: Automatic createdAt and updatedAt management
- **Transactional Service**: @Transactional on service methods for data consistency
- **Composite Keys**: Proper many-to-many relationship modeling
- **Optimistic Locking**: Timestamp-based conflict detection

## Development Best Practices

### When Adding New Features
1. **Controller Layer**: Define REST endpoints with proper Swagger annotations
2. **Service Layer**: Implement business logic with transaction management
3. **Repository Layer**: Create JPA repository interfaces for data access
4. **DTO Layer**: Create request/response DTOs for API contracts
5. **Exception Handling**: Use existing custom exceptions or create new ones
6. **Testing**: Write unit tests and integration tests with Jacoco coverage

### Code Standards
- Use Lombok annotations to reduce boilerplate code
- Follow Spring Boot best practices for layered architecture
- Implement proper exception handling with meaningful error messages
- Use @Transactional on service methods that modify data
- Document APIs with Swagger annotations (@Operation, @Tag)
- Separate read and write operations (CQRS pattern: Service vs Reader)

### Configuration Management
- Use Spring Cloud Config for externalized configuration
- Profile-specific configurations in application-{profile}.yaml
- Sensitive data (credentials, API keys) should be in external config server
- Use @Value annotation for injecting configuration properties

### Monitoring and Observability
- Spring Actuator endpoints enabled for health checks
- Prometheus metrics via Micrometer
- Sentry integration for error tracking and alerting
- Structured logging for troubleshooting

## Common Operations

### Adding New User Fields
1. Update `Users` entity with new fields
2. Update `UsersUpdateRequest` DTO if field is editable
3. Update `UsersResponse` DTO for API responses
4. Modify `UsersService.update()` method
5. Run database migration if needed

### Adding New Feign Clients
1. Create new interface in `clients/` package
2. Annotate with `@FeignClient(name = "service-name")`
3. Define API methods with Spring MVC annotations
4. Inject client in service layer
5. Handle FeignException for error scenarios

### Adding New Endpoints
1. Create new methods in appropriate controller
2. Add Swagger documentation (@Operation, @ApiResponse)
3. Implement business logic in service layer
4. Create DTOs for request/response if needed
5. Add validation annotations on request DTOs
6. Handle exceptions properly

### Database Schema Changes
1. Update JPA entity classes
2. Test changes locally with dev profile
3. Generate migration scripts if using Flyway/Liquibase
4. Update composite key classes if relationship changes
5. Update repository queries if needed
