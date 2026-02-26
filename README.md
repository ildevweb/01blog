# 01Blog

## Overview

**01Blog** is a fullstack social blogging platform designed to help students document and share their learning journey. Users can create posts with media, follow other students, interact through likes and comments, receive notifications, and report inappropriate behavior. Administrators have dedicated tools to moderate content and manage users.

This project is built with **Spring Boot** for the backend and **Angular** for the frontend, following RESTful architecture and secure authentication practices.

---

## Project roadmap
https://excalidraw.com/#json=ToXKl6eowdkYU46aTqnK5,IXkLwp47JwjAAPWNFl_hLg

---

## Features

### Authentication & Security

* User registration and login
* Secure password hashing
* JWT-based authentication
* Role-based access control (USER / ADMIN)
* Protected routes on both backend and frontend

### Users & Profiles

* Public user profile ("block") displaying all posts
* Follow / unfollow users
* View other users’ blocks

### Posts

* Create, edit, and delete posts
* Support for text, images, and videos
* Timestamped posts with media preview
* Like and comment on posts

### Notifications

* Receive notifications when followed users publish posts
* Notification list with read/unread state

### Reports & Moderation

* Report users for inappropriate or offensive content
* Reports include reason and timestamp
* Reports visible only to administrators

### Admin Panel

* View and manage all users
* Delete or ban users/posts
* Handle user reports
* All admin routes secured by role-based access

---

## Technologies Used

### Backend

* Java 17
* Spring Boot
* Spring Security
* JWT (JSON Web Tokens)
* Spring Data JPA / Hibernate
* MySQL (relational database)
* Maven

### Frontend

* Angular
* TypeScript
* Bootstrap (responsive UI)
* Angular Router
* HTTP Interceptors & Guards

### Tools & Practices

* Git & GitHub
* RESTful API design
* Layered architecture (Controller / Service / Repository)

---

## Project Structure

```
📁 01blog
├─ 📁 back-end
│  ├─ 📁 .mvn
│  │  └─ 📁 wrapper
│  │     └─ 📄 maven-wrapper.properties
│  ├─ 📁 src
│  │  ├─ 📁 main
│  │  │  ├─ 📁 java
│  │  │  │  └─ 📁 com
│  │  │  │     └─ 📁 example
│  │  │  │        └─ 📁 app
│  │  │  │           ├─ 📁 controller
│  │  │  │           │  ├─ 📄 AdminController.java
│  │  │  │           │  ├─ 📄 AuthController.java
│  │  │  │           │  ├─ 📄 CommentController.java
│  │  │  │           │  ├─ 📄 NotificationController.java
│  │  │  │           │  ├─ 📄 PostController.java
│  │  │  │           │  ├─ 📄 ReportController.java
│  │  │  │           │  └─ 📄 UserController.java
│  │  │  │           ├─ 📁 dto
│  │  │  │           │  ├─ 📄 CommentInfos.java
│  │  │  │           │  ├─ 📄 CommentRequest.java
│  │  │  │           │  ├─ 📄 LikeRequest.java
│  │  │  │           │  ├─ 📄 LoginRequest.java
│  │  │  │           │  ├─ 📄 LoginResponse.java
│  │  │  │           │  ├─ 📄 NotificationInfos.java
│  │  │  │           │  ├─ 📄 PostInfos.java
│  │  │  │           │  ├─ 📄 RegisterRequest.java
│  │  │  │           │  ├─ 📄 ReportInfos.java
│  │  │  │           │  ├─ 📄 ReportRequest.java
│  │  │  │           │  └─ 📄 UserInfos.java
│  │  │  │           ├─ 📁 entity
│  │  │  │           │  ├─ 📄 Comment.java
│  │  │  │           │  ├─ 📄 CommentLike.java
│  │  │  │           │  ├─ 📄 Follow.java
│  │  │  │           │  ├─ 📄 Notification.java
│  │  │  │           │  ├─ 📄 Post.java
│  │  │  │           │  ├─ 📄 PostLike.java
│  │  │  │           │  ├─ 📄 Report.java
│  │  │  │           │  └─ 📄 User.java
│  │  │  │           ├─ 📁 repository
│  │  │  │           │  ├─ 📄 CommentLikeRepository.java
│  │  │  │           │  ├─ 📄 CommentRepository.java
│  │  │  │           │  ├─ 📄 FollowRepository.java
│  │  │  │           │  ├─ 📄 NotificationRepository.java
│  │  │  │           │  ├─ 📄 PostLikeRepository.java
│  │  │  │           │  ├─ 📄 PostRepository.java
│  │  │  │           │  ├─ 📄 ReportRepository.java
│  │  │  │           │  └─ 📄 UserRepository.java
│  │  │  │           ├─ 📁 security
│  │  │  │           │  ├─ 📄 JwtAuthenticationFilter.java
│  │  │  │           │  ├─ 📄 JwtService.java
│  │  │  │           │  ├─ 📄 JwtUtil.java
│  │  │  │           │  ├─ 📄 RateLimitFilter.java
│  │  │  │           │  ├─ 📄 SecurityConfig.java
│  │  │  │           │  ├─ 📄 UserPrincipal.java
│  │  │  │           │  └─ 📄 WebConfig.java
│  │  │  │           ├─ 📁 service
│  │  │  │           │  ├─ 📄 AuthService.java
│  │  │  │           │  ├─ 📄 CommentLikeService.java
│  │  │  │           │  ├─ 📄 CommentService.java
│  │  │  │           │  ├─ 📄 FollowService.java
│  │  │  │           │  ├─ 📄 NotificationService.java
│  │  │  │           │  ├─ 📄 PostLikeService.java
│  │  │  │           │  ├─ 📄 PostService.java
│  │  │  │           │  ├─ 📄 ReportService.java
│  │  │  │           │  └─ 📄 UserService.java
│  │  │  │           └─ 📄 AppApplication.java
│  │  │  └─ 📁 resources
│  │  │     └─ 📄 application.properties
│  │  └─ 📁 test
│  │     └─ 📁 java
│  │        └─ 📁 com
│  │           └─ 📁 example
│  │              └─ 📁 app
│  │                 └─ 📄 AppApplicationTests.java
│  ├─ 📁 uploads
│  ├─ 📁 .gitattributes
│  ├─ 📁 .gitignore
│  ├─ 🐳 Dockerfile
│  ├─ 📄 mvnw
│  ├─ 📄 mvnw.cmd
│  └─ 📄 pom.xml
├─ 📁 front-end
│  ├─ 📁 .angular
│  ├─ 📁 public
│  │  └─ 📄 favicon.ico
│  ├─ 📁 src
│  │  ├─ 📁 app
│  │  │  ├─ 📁 core
│  │  │  │  ├─ 📁 auth
│  │  │  │  │  ├─ 📘 auth.guard.ts
│  │  │  │  │  ├─ 📘 auth.service.ts
│  │  │  │  │  ├─ 📘 guest.guard.ts
│  │  │  │  │  └─ 📘 token.interceptor.ts
│  │  │  │  ├─ 📁 services
│  │  │  │  │  └─ 📘 notification.service.ts
│  │  │  │  └─ 📘 core.module.ts
│  │  │  ├─ 📁 pages
│  │  │  │  ├─ 📁 404
│  │  │  │  │  ├─ 🎨 404.component.css
│  │  │  │  │  ├─ 🌐 404.component.html
│  │  │  │  │  └─ 📘 404.component.ts
│  │  │  │  ├─ 📁 admin
│  │  │  │  │  ├─ 🎨 admin.component.css
│  │  │  │  │  ├─ 🌐 admin.component.html
│  │  │  │  │  └─ 📘 admin.component.ts
│  │  │  │  ├─ 📁 auth
│  │  │  │  │  ├─ 🎨 auth.component.css
│  │  │  │  │  ├─ 🌐 auth.component.html
│  │  │  │  │  └─ 📘 auth.component.ts
│  │  │  │  ├─ 📁 home
│  │  │  │  │  ├─ 📁 components
│  │  │  │  │  │  ├─ 📁 comments-modal
│  │  │  │  │  │  ├─ 📁 create-post-form
│  │  │  │  │  │  │  ├─ 🌐 create-post-form.component.html
│  │  │  │  │  │  │  └─ 📘 create-post-form.component.ts
│  │  │  │  │  │  ├─ 📁 suggested-users-sidebar
│  │  │  │  │  │  │  ├─ 🌐 suggested-users-sidebar.component.html
│  │  │  │  │  │  │  └─ 📘 suggested-users-sidebar.component.ts
│  │  │  │  │  ├─ 🎨 home.component.css
│  │  │  │  │  ├─ 🌐 home.component.html
│  │  │  │  │  └─ 📘 home.component.ts
│  │  │  │  └─ …(other pages: notification, profile)
│  │  │  ├─ 📁 shared
│  │  │  │  └─ 📁 components
│  │  │  │     └─ …(comments-modal, edit-post-modal, navbar, post-card, report-modal)
│  │  │  ├─ 📘 app.config.ts
│  │  │  ├─ 📘 app.routes.ts
│  │  │  ├─ 📘 app.spec.ts
│  │  │  └─ 📘 app.ts
│  │  ├─ 📁 assets
│  │  │  └─ 📁 images
│  │  ├─ 🌐 index.html
│  │  ├─ 📘 main.ts
│  │  └─ 🎨 styles.css
│  ├─ 📄 .editorconfig
│  ├─ 📁 .gitignore
│  ├─ 🐳 Dockerfile
│  ├─ ⚙️ angular.json
│  ├─ 📄 default.conf
│  ├─ ⚙️ package-lock.json
│  ├─ 📦 package.json
│  ├─ ⚙️ tsconfig.app.json
│  ├─ ⚙️ tsconfig.json
│  └─ ⚙️ tsconfig.spec.json
├─ 📝 README.md
└─ ⚙️ docker-compose.yml
```

---

## Run with Docker

Make sure you have **Docker** and **Docker Compose** installed.  

```bash
docker-compose up
````
---

## Authentication Flow

* Users authenticate using JWT
* Token is stored on the client
* Angular HTTP interceptor attaches the token to requests
* Backend validates token on protected routes
* Invalid or expired tokens return HTTP 401 (Unauthorized)

This behavior follows security best practices and HTTP standards.

---

## Media Storage

* Uploaded images and videos are stored on the server file system
* Media paths are saved in the database

---

## Evaluation Criteria Coverage

* ✅ Functional features implemented (posts, likes, comments, follows, reports, admin tools)
* 🔐 Secure authentication and role-based access control
* 🎨 Responsive and clean UI using Bootstrap
* 📄 Detailed README with setup instructions and technologies

---

## Author

Developed by **Ilyass Afriad**
GitHub: [https://github.com/IlyassAfriad](https://github.com/IlyassAfriad)
