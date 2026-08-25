# Fatec ADS

Academic web system built with Spring Boot for managing users, courses, professors, students, subjects, products, orders, and order items. The project uses server-rendered Thymeleaf pages, Spring Security authentication, and PostgreSQL persistence.

## Overview

Fatec ADS centralizes basic registration and search operations in a simple administrative environment. The interface follows a clean and direct design approach inspired by Material Design, Human Interface principles, responsive design, accessibility, and user-centered experience.

## Features

- User login and logout.
- Public user registration.
- Password recovery by e-mail token.
- Course listing, creation, editing, and deletion.
- Professor listing, creation, editing, and deletion.
- Student listing, creation, editing, and deletion.
- Subject listing, creation, editing, and deletion.
- Product listing, creation, editing, and deletion.
- Order registration with products and totals.
- Order item listing, creation, editing, and deletion.
- Student association with courses.
- Subject association with courses and professors.

## Technologies

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Lombok
- Maven Wrapper

## Requirements

Before running the project, install or configure:

- JDK 17 or later.
- PostgreSQL running locally.
- A database named `fatecads_db`.
- A database user with PostgreSQL access.

The default settings are in `src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fatecads_db
spring.datasource.username=postgres
spring.datasource.password=123456
server.port=8080
```

If necessary, change the username, password, port, or database name according to the local environment.

## Database Setup

Create the database in PostgreSQL:

```sql
CREATE DATABASE fatecads_db;
```

The project is configured with:

```properties
spring.jpa.hibernate.ddl-auto=update
```

With this setting, Hibernate creates or updates the tables automatically when the application starts.

## E-mail Setup

Password recovery uses Gmail SMTP. In `application.properties`, configure a valid e-mail account and an application password:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from=your_email@gmail.com
```

For Gmail, use an app password generated in the Google account. Do not use the main account password.

## How to Run

From the project root, run:

```bash
./mvnw spring-boot:run
```

On Windows, you can also use:

```bash
mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080/fatecads
```

## Tests

To run automated tests:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

## Main Routes

| Route | Description |
| --- | --- |
| `/fatecads` | Public landing page |
| `/login` | Login page |
| `/home` | Home page after authentication |
| `/users/create` | User registration |
| `/users/list` | User list |
| `/forgot-password` | Password recovery request |
| `/reset-password` | Password reset by token |
| `/course/list` | Course list |
| `/course/new` | Course registration |
| `/professor/list` | Professor list |
| `/professor/new` | Professor registration |
| `/student/list` | Student list |
| `/student/new` | Student registration |
| `/subject/list` | Subject list |
| `/subject/new` | Subject registration |
| `/product/list` | Product list |
| `/product/new` | Product registration |
| `/order/create` | Order registration |
| `/item-of-order/list` | Order item list |
| `/item-of-order/new` | Order item registration |

## Project Structure

```text
src/
  main/
    java/br/com/fatecads/fatecads/
      config/       Security and authentication configuration
      controller/   MVC controllers
      entity/       JPA entities
      repository/   Database access interfaces
      service/      Business rules
    resources/
      static/css/   Application styles
      templates/    Thymeleaf pages
      application.properties
  test/
    java/           Automated tests
```

## Data Model

Main entities:

- `User`: system user, credentials, role, and password recovery token.
- `Course`: course, period, and workload hours.
- `Professor`: professor, phone, graduation, and RM.
- `Student`: student, personal data, registration number, and linked course.
- `Subject`: subject, code, workload hours, linked course, and linked professor.
- `Product`: product description, price, unit, and brand.
- `Order`: order date, student, items, and total.
- `ItemOfOrder`: order item, amount, price, subtotal, product, and order.

## Security

The application uses Spring Security with:

- Passwords encrypted with BCrypt.
- Custom login at `/login`.
- Redirect to `/home` after login.
- Logout returning to `/login?logout`.
- Public access to login, user registration, password recovery, landing page, and static file routes.
- All other routes protected by authentication.

## Design Notes

The project can evolve while keeping principles already validated for this type of system:

- Minimalist and responsive layout.
- Clear navigation for administrative routines.
- Consistent components following a design system idea.
- Accessibility in forms, buttons, contrast, and error messages.
- Objective experience for academic and administrative use.

## Author

Academic project developed in the Fatec ADS context.
