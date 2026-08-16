# InvoicePro — Invoice Generator

A full-stack invoice management system built with Spring Boot, featuring a live dashboard, dynamic invoice creation, AI-assisted line item descriptions, and downloadable PDF invoices.

Built as a technical assessment project, this application demonstrates layered backend architecture, accurate financial calculation logic, and a clean, functional dashboard UI.

---

## Features

- **Dashboard** — real-time overview of total revenue, invoice counts, and paid/unpaid status, with search and status filtering
- **Client management** — add clients on the fly via an inline modal, no page reload required
- **Dynamic invoice creation** — add/remove line items with live-calculated subtotal, tax, discount, and grand total
- **AI-assisted descriptions** — turns a rough note (e.g. *"logo design 3 revisions"*) into a professional invoice line item using the Gemini API
- **Printable invoice view** — clean, print-optimized layout
- **PDF export** — generates a downloadable, properly formatted invoice PDF (via OpenPDF)
- **Invoice lifecycle** — mark invoices as Paid, with dashboard stats updating accordingly
- **Validated, tested calculation logic** — `BigDecimal`-based money handling, capped discounts, sequential per-year invoice numbering, covered by JUnit tests

---

## Tech Stack

**Backend**
- Java 17, Spring Boot 3.x
- Spring MVC, Spring Data JPA, Hibernate
- Bean Validation (Jakarta Validation)
- MySQL

**Frontend**
- Thymeleaf (server-rendered templates)
- Bootstrap 5 + Bootstrap Icons
- Vanilla JavaScript (dynamic form behavior, AJAX calls to REST endpoints)

**Other**
- OpenPDF — PDF generation
- Google Gemini API — AI-generated invoice descriptions
- JUnit 5 + Mockito — unit testing
- Maven — build tool

---

## Architecture

The project follows a standard layered architecture:

```
Controller  →  Service  →  Repository  →  Database
    ↓
   DTOs (request/response shaping, validation)
```

- **Controllers** — expose REST APIs (`/api/**`) and server-rendered views (`/`, `/invoices/**`)
- **Services** — contain all business logic (invoice calculations, PDF generation, AI description generation)
- **Repositories** — Spring Data JPA interfaces for persistence
- **DTOs** — decouple the API contract from database entities; prevent clients from sending or receiving internal-only fields
- **Global exception handler** — converts validation errors and not-found exceptions into consistent JSON responses instead of raw stack traces

### Key design decisions

| Decision | Reason |
|---|---|
| `BigDecimal` for all monetary values | Avoids floating-point rounding errors inherent to `double`/`float` |
| DTOs instead of exposing entities | Keeps the API contract stable and independent of schema changes; avoids leaking internal fields |
| Discount capped at subtotal + tax | Prevents a negative grand total from invalid input |
| Invoice numbers reset per calendar year | Mirrors real-world invoicing conventions (e.g. `INV-2026-0001`) |
| OpenPDF over iText 5+ | LGPL/MPL licensing is safe for unrestricted use, unlike iText 5+'s AGPL license |
| Service-layer unit tests with mocked repository | Verifies calculation correctness independent of the database |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL 8+
- A Gemini API key ([Google AI Studio](https://aistudio.google.com/apikey)) — optional, only required for the AI description feature

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/invoice-generator.git
   cd invoice-generator
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE invoice_db;
   ```

3. **Configure application properties**

   Copy the example config and fill in your own values:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   gemini.api.key=YOUR_GEMINI_API_KEY
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Open the dashboard**

   Visit [http://localhost:8080](http://localhost:8080)

### Running tests

```bash
mvn test
```

---

## API Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/clients` | Create a client |
| `GET` | `/api/clients` | List all clients |
| `GET` | `/api/clients/{id}` | Get a client by ID |
| `POST` | `/api/invoices` | Create an invoice |
| `GET` | `/api/invoices` | List all invoices |
| `GET` | `/api/invoices/{id}` | Get an invoice by ID |
| `PATCH` | `/api/invoices/{id}/status?status=PAID` | Update invoice status |
| `DELETE` | `/api/invoices/{id}` | Delete an invoice |
| `GET` | `/api/invoices/{id}/pdf` | Download invoice as PDF |
| `POST` | `/api/ai/generate-description` | Generate a professional line item description from a short note |

---

## Project Structure

```
src/main/java/com/invoiceapp/invoicegenerator/
├── controller/       REST and web (Thymeleaf) controllers
├── service/          Business logic (invoice calc, PDF, AI)
├── repository/        Spring Data JPA repositories
├── entity/            JPA entities (Client, Invoice, InvoiceItem)
├── dto/                Request/response DTOs
└── exception/          Global exception handling

src/main/resources/
├── templates/         Thymeleaf HTML views
└── application.properties.example
```

---

## Known Limitations / Next Steps

This was built as a scoped technical assessment. In a production context, the following would be prioritized next:

- **Authentication & authorization** — currently all endpoints are open; would add Spring Security with JWT
- **Concurrency safety** — invoice number generation is not currently atomic under concurrent requests; would move to a database sequence or a `SELECT ... FOR UPDATE` pattern
- **Pagination** — invoice/client lists currently load in full; fine at current scale, would paginate for production volume
- **Rate limit handling** — the Gemini API free tier has low request limits; would add debouncing on the frontend and retry-with-backoff on the backend
- **Broader test coverage** — current tests cover the service layer; would extend to controller-level integration tests

---

## License

This project was built for evaluation purposes.
