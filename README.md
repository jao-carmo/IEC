# Fatec ADS

An English-language academic workspace and shopping demo built with Java 17, Spring Boot, Thymeleaf, Spring Security and PostgreSQL. The responsive interface keeps the existing minimalist layout, purple accent, shared components, keyboard focus states and reduced-motion support.

## Access and features

| Feature | User | Administrator |
| --- | --- | --- |
| Product catalog, shopping cart and checkout | Yes | Yes |
| Personal order history and receipts | Own orders only | Own orders only |
| Users and role assignment | No | Yes |
| Students, courses, professors and subjects | No | Yes |
| Product creation, editing and deletion | No | Yes |
| All orders and administrative order creation | No | Yes |
| Maintenance of administrative order items | No | Yes |

Public registration always creates `ROLE_USER`, regardless of submitted fields. Only administrators can assign `ROLE_ADMIN`. Existing roles `ADMIN` and `USER` are accepted at sign-in as well. Sign out and sign in again after a role change so the session receives the updated permissions.

## Run on Windows

Requirements: PostgreSQL and JDK 17 or 21. Java 27 is incompatible with the current Lombok/compiler combination. Set your IDE project SDK to Java 17.

Create the database if needed:

```sql
CREATE DATABASE fatecads_db;
```

Defaults preserve the existing local academic setup: `localhost:5432/fatecads_db`, user `postgres`, password `123456`. Override these with `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` for your environment.

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev.ps1
```

The script uses the local JDK in `.tools/java17` when present; otherwise it uses `JAVA_HOME`. It does not install Java. Alternatively, with a compatible JDK configured:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). Hibernate updates the existing schema and adds the order-to-user relationship. Historical administrative orders remain available to administrators; they are not assigned to arbitrary users.

## Initial root account

- Login: `root`.
- A unique local password is configured in the ignored `application-local.properties`; see the ignored `root-credentials.local.txt` for the credentials in this workspace.
- The account is created on the first successful application startup. Existing root credentials are never overwritten by restarting the app.
- On a fresh clone, set `ROOT_PASSWORD` (12 to 72 characters) and optionally `ROOT_EMAIL` before starting. Without a configured password, no root account is seeded.
- The reserved root administrator cannot be publicly registered, renamed, demoted or deleted. Its password can be changed in **Manage users**.
- Local credentials and `.tools` are excluded from Git. Do not commit passwords.

## Shopping flow

1. Register and sign in, then open **Browse products**.
2. Add products to the cart. Adding the same product merges its quantity.
3. Change quantities (1 to 999), remove individual items, or clear the cart.
4. Confirm the order to save the purchase and open **My orders**.
5. Open a receipt to review the recorded quantities and prices.

The cart is isolated by HTTP session and contains IDs and quantities only. It expires with the session (30 minutes idle) and is discarded on sign-out. Prices are loaded from the database; calculations use decimal arithmetic rounded to cents. Checkout checks prices again, commits the order and items together, and clears the cart only after success. Missing products remain removable and block checkout. A repeated confirmation of the now-empty cart does not create another order.

This is an academic checkout: it records an order without charging a payment, calculating shipping/taxes, or managing stock. Confirmed customer orders cannot be modified through the administrative item editor. Administrative orders retain the student-linked workflow, and item changes recalculate their order totals.

## Security and validation

- Server-enforced role rules, ownership checks and CSRF protection for every write, including AJAX and logout.
- Deletions use POST forms; linked records are protected by database foreign keys with readable conflict errors.
- BCrypt passwords; a blank password on an administrative edit preserves the existing hash. Passwords require at least 12 characters and at most 72 UTF-8 bytes.
- Login/email uniqueness checks and database constraints. Resolve any existing duplicates before applying those constraints to an older database.
- Password reset responses do not reveal whether an email exists. Expiring tokens are stored as SHA-256 hashes and consumed once.
- Reset URLs use configured `APP_BASE_URL`, not the incoming Host header. Old plaintext recovery tokens must be requested again after this update.
- Product uploads are checked by file content and dimensions: PNG/JPEG only, up to 5 MB and 20 megapixels. Submitted image paths are ignored; edits preserve the existing image.
- Server-side validation covers quantities, prices, academic field limits and workloads. CPF and phone punctuation is removed before persistence; this validates CPF length, not its check digits.
- Session cookies use HttpOnly and SameSite=Lax. Set `COOKIE_SECURE=true` when serving over HTTPS.

## Email and environment

Password recovery requires a working Gmail SMTP account and app password. Set `MAIL_USERNAME`, `MAIL_PASSWORD` and `MAIL_FROM`. No delivery is possible until SMTP is configured; requests still show the same privacy-preserving response. SMTP errors are logged without reset URLs.

| Variable | Default / purpose |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/fatecads_db` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `123456` (local academic setup) |
| `ROOT_PASSWORD` | Initial root seed; no shared default |
| `ROOT_EMAIL` | `root@fatecads.local` |
| `APP_BASE_URL` | `http://localhost:8080` |
| `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` | SMTP credentials and sender |
| `COOKIE_SECURE` | `false` locally; `true` for HTTPS |

Use `application-local.properties` for machine-specific configuration; it is loaded automatically and excluded from Git.

## Verification

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev.ps1 test
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\dev.ps1 package
```

Automated integration tests run against an isolated H2 database in PostgreSQL mode, without changing the local PostgreSQL data or sending emails. They exercise authentication, registration tampering, role restrictions, CSRF, template rendering, session isolation, cart changes, checkout price tampering, order ownership, upload validation, password preservation/recovery, academic CRUD and administrative order totals.

## Main routes

| Route | Purpose |
| --- | --- |
| `/`, `/fatecads` | Public landing page |
| `/login`, `/users/create` | Sign in / public registration |
| `/forgot-password`, `/reset-password` | Password recovery |
| `/home` | Role-aware workspace |
| `/product/list` | Shared catalog |
| `/cart` | Session cart and checkout |
| `/purchases`, `/purchases/{id}` | Personal orders / receipt |
| `/users/list` | Administrator user management |
| `/student/list`, `/course/list`, `/professor/list`, `/subject/list` | Academic administration |
| `/order/list`, `/order/create`, `/order/{id}` | All orders, student order creation and details |
| `/item-of-order/list` | Administrative item maintenance |

Source layout: `config` for authentication and startup, `controller` for MVC routes, `service` for business rules, `entity` for persistence, `repository` for data access, `templates` for English pages, and `static/css/app.css` for the shared design system.

## Academic records and linked logins

Student and professor registration now include **System access → Create login account**. Check it and enter a unique login and initial password to save both the academic record and its linked account in one transaction. For students, the account uses the student's email (maximum 40 characters); professors have an account email field. All accounts created this way receive the **User** role. Professors do not automatically receive administrator privileges.

Existing students and professors can receive a login by editing their record. **Manage users** is available in both lists and forms; linked records display **Manage account** for password, email and permission changes. Editing an academic record preserves its existing link. Changing academic contact information does not automatically change login credentials; use **Manage account** to update those. Removing an academic record does not delete its login account, and linked accounts cannot be deleted until their academic dependencies are resolved.

Students have an **Academic status** field with **In progress** and **Completed**. Existing records default to **In progress**. The student list shows the assigned course and status, with summary counts. A student who signs in with a linked account sees their own assigned course and academic status in the workspace. Linked professors see their teaching profile.

## Bundled demonstration data

Demo data is enabled by default (`DEMO_DATA_ENABLED=true`). Starting against a fresh PostgreSQL database inserts:

- 8 products, including the notebook, with locally bundled images.
- 3 courses and 3 professors.
- 6 subjects linked to courses and professors.
- 6 fictitious students assigned to courses, including ongoing and completed studies.

The fixture definitions live in `DemoDataService.java`, and the product artwork lives in `src/main/resources/static/images/demo`. These are repository files: after cloning the repository and configuring PostgreSQL, start the application to populate the database. No database dump or external image service is required. Existing records matching the demo names or registration codes are reused without being overwritten; the existing notebook keeps its price and uploaded image.

The entire load is transactional and recorded in `demo_data_version` as `academic-store-v1`. Subsequent startups do not duplicate data, restore deleted demo records or undo edits. A new database receives the initial data again. Set `DEMO_DATA_ENABLED=false` to skip automatic loading. Demo academic records do not contain shared login passwords; create their login through the registration form to test student or professor access.

The store includes a responsive product grid, search by product/brand, price filters and sorting. The cart uses product cards and an order summary while preserving server-side price calculation and checkout protections.
