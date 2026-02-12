# 01Blog

## Overview

**01Blog** is a fullstack social blogging platform designed to help students document and share their learning journey. Users can create posts with media, follow other students, interact through likes and comments, receive notifications, and report inappropriate behavior. Administrators have dedicated tools to moderate content and manage users.

This project is built with **Spring Boot** for the backend and **Angular** for the frontend, following RESTful architecture and secure authentication practices.

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
01blog/
├── back-end/      # Spring Boot application
├── front-end/     # Angular application
├── README.md      # Project documentation
```

---

## Backend Setup

### Prerequisites

* Java 17+
* Maven
* MySQL

### Configuration

Update database credentials in:

```
back-end/src/main/resources/application.properties
```

Example:

```
spring.datasource.url=jdbc:mysql://localhost:3306/01blog_db
spring.datasource.username=root
spring.datasource.password=admin
```

### Run Backend

```bash
cd back-end
mvn spring-boot:run
```

The backend will run on:

```
http://localhost:8080
```

---

## Frontend Setup

### Prerequisites

* Node.js (v18+ recommended)
* Angular CLI

### Install Dependencies

```bash
cd front-end
npm install
```

### Run Frontend

```bash
ng serve
```

The frontend will be available at:

```
http://localhost:4200
```

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
