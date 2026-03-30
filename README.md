# Rebook User Service

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)](https://gradle.org/)

Rebook 플랫폼의 사용자 관리 마이크로서비스. 사용자 프로필, 선호 카테고리, 내부 통신 API를 제공합니다.

## 목차

- [아키텍처](#아키텍처)
- [기능](#기능)
- [기술 스택](#기술-스택)
- [API 문서](#api-문서)
- [프로젝트 구조](#프로젝트-구조)

---

## 아키텍처
- 유저생성
![유저생성](https://diagrams-noaahh.s3.ap-northeast-2.amazonaws.com/user_create.png)

---

## 기능

### 사용자 관리
- **프로필 조회/수정**: 닉네임, 프로필 이미지 관리 (S3 업로드)
- **계정 삭제**: 사용자 탈퇴 처리
- **비밀번호 변경**: 사용자 비밀번호 업데이트

### 선호 카테고리
- **카테고리 등록/조회**: 사용자 선호 카테고리 CRUD
- **카테고리 기반 사용자 검색**: 특정 카테고리를 선호하는 사용자 목록 조회 (알림 서비스 연동)
- **추천 카테고리 조회**: 사용자 맞춤 카테고리 추천

### 내부 통신 (Internal API)
- **회원가입**: 다른 서비스에서 호출하는 사용자 생성 API
- **작가 정보 조회**: 도서 서비스에서 활용하는 작가 정보 API
- **알림 대상 조회**: 카테고리 기반 알림 대상 사용자 ID 목록 제공

---

## 기술 스택

### Language & Framework
- **Java 17**, **Spring Boot 3.x**, **Spring Cloud**

### Database
- **PostgreSQL**, **QueryDSL**, **Spring Data JPA**

### Messaging
- **RabbitMQ** (AMQP) — 비동기 메시지 처리

### Cloud & Infrastructure
- **Spring Cloud Config** — 중앙 설정 관리
- **Netflix Eureka** — 서비스 디스커버리
- **OpenFeign** — 서비스 간 HTTP 통신
- **AWS S3** — 프로필 이미지 스토리지

### Monitoring & Docs
- **Actuator**, **Prometheus**, **Sentry**
- **Spotless** (코드 포맷팅), **Jacoco** (테스트 커버리지)

### Build & Deploy
- **Gradle**, **Docker**

---

## API 문서
Apidog에서 확인하실 수 있습니다:

```
https://x6wq8qo61i.apidog.io/
```

### External API (`/api/users`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/users/test` | 헬스 체크 (Passport 인증 테스트) |
| GET | `/api/users` | 내 프로필 조회 |
| GET | `/api/users/{userId}` | 특정 사용자 프로필 조회 |
| PUT | `/api/users` | 프로필 수정 (이미지 포함) |
| DELETE | `/api/users` | 계정 삭제 |
| PATCH | `/api/users/me` | 비밀번호 변경 |
| GET | `/api/users/categories` | 내 선호 카테고리 조회 |
| POST | `/api/users/categories` | 선호 카테고리 추가 |
| GET | `/api/users/categories/recommendations/{userId}` | 추천 카테고리 조회 |

### Internal API (`/internal/users`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/internal/users/sign-up` | 사용자 생성 (타 서비스 호출용) |
| POST | `/internal/users/authors` | 작가 정보 조회 |
| GET | `/internal/users/alarms/books?category=` | 카테고리 기반 알림 대상 사용자 ID 목록 |

---

## 프로젝트 구조

```
src/main/java/.../rebookuserservice/
├── clientfeign/              # 외부 서비스 통신 (NotificationClient)
│   └── notification/
├── common/                   # 공통 예외 정의
│   └── exception/
├── config/                   # 인프라 설정 (Redis, S3 등)
├── domain/
│   ├── controller/           # REST API (UsersController, InternalController)
│   ├── model/
│   │   ├── dto/              # Request/Response DTO
│   │   └── entity/           # JPA Entity (Users, FavoriteCategory)
│   ├── repository/           # JPA Repository
│   └── service/              # 비즈니스 로직 (reader/writer 분리)
│       ├── reader/
│       └── writer/
└── external/
    └── s3/                   # AWS S3 설정 및 파일 업로드
```