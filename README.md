
# Charity Collection Box Management API

This is a backend application for managing collection boxes during fundraising events for charity organizations. It is built using Java, Spring Boot, Spring Data JPA, and an H2 in-memory database.

## System Requirements

- Java Development Kit (JDK) version 21 or newer.
- Apache Maven 3.6+ (for building the project).
- Internet access (for downloading Maven dependencies during the build).

## Building the Application

### 1. Clone the Repository
```bash
git clone https://github.com/macioooo/CollectionBoxes.git
cd CollectionBoxes
```
*(If you are working on a specific branch, make sure to check it out, e.g., `git checkout your-branch-name`)*

### 2. Build with Maven
```bash
mvn clean package
```
This will produce a JAR file in the `target/` directory (e.g., `CollectionBoxes-1.0-SNAPSHOT.jar`).

## Running the Application
```bash
java -jar target/CollectionBoxes-1.0-SNAPSHOT.jar
```
The Spring Boot application will start, and by default, the embedded Tomcat server will listen on **port 8080**.

## Accessing the H2 Database Console
- **H2 Console URL:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **JDBC URL:** `jdbc:h2:mem:collectionboxesdb`
- **User Name:** `sa`
- **Password:** *(leave blank)*

This console allows you to inspect tables and run SQL queries against the in-memory database.

## REST API Endpoints

> Base URL: `http://localhost:8080`

### Fundraising Events Module
Base Path: `/api/v1/fundraising-events`

### 1. Create a New Fundraising Event
- **Method:** POST
- **URL:** `/api/v1/fundraising-events/register`
- **Request Body:**
```json
{
  "nameOfTheFundraisingEvent": "Winter Aid Campaign 2025",
  "currencyOfTheMoneyAccount": "PLN"
}
```
- **Sample cURL:**
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"nameOfTheFundraisingEvent": "Winter Aid Campaign 2025", "currencyOfTheMoneyAccount": "PLN"}' \
http://localhost:8080/api/v1/fundraising-events/register
```

### 2. Display Financial Report
- **Method:** GET
- **URL:** `/api/v1/fundraising-events/report`
- **Sample cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/fundraising-events/report
```

### 3. Transfer Money from a Collection Box
- **Method:** POST
- **URL:** `/api/v1/fundraising-events/moneyTransfer`
- **Query Param:** `collectionBoxId`
- **Sample cURL:**
```bash
curl -X POST "http://localhost:8080/api/v1/fundraising-events/moneyTransfer?collectionBoxId=your-collection-box-id"
```

---

### Collection Boxes Module
Base Path: `/api/v1/collection-boxes`

### 1. Register a New Collection Box
- **Method:** POST
- **URL:** `/api/v1/collection-boxes/register`
- **Sample cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/collection-boxes/register
```

### 2. Assign Collection Box to Fundraising Event
- **Method:** POST
- **URL:** `/api/v1/collection-boxes/{collectionBoxId}/assign`
- **Query Param:** `fundraisingEventId`
- **Sample cURL:**
```bash
curl -X POST "http://localhost:8080/api/v1/collection-boxes/your-collection-box-id/assign?fundraisingEventId=your-fundraising-event-id"
```

### 3. List All Collection Boxes
- **Method:** GET
- **URL:** `/api/v1/collection-boxes`
- **Sample cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/collection-boxes
```

### 4. Delete a Collection Box
- **Method:** DELETE
- **URL:** `/api/v1/collection-boxes/delete/{collectionBoxId}`
- **Sample cURL:**
```bash
curl -X DELETE http://localhost:8080/api/v1/collection-boxes/delete/your-collection-box-id
```

### 5. Add Funds to a Collection Box
- **Method:** POST
- **URL:** `/api/v1/collection-boxes/{collectionBoxId}/add-funds`
- **Request Body:**
```json
{
  "currency": "USD",
  "amount": 25.50
}
```
- **Sample cURL:**
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"currency": "USD", "amount": 25.50}' \
http://localhost:8080/api/v1/collection-boxes/your-collection-box-id/add-funds
```

### 6. Optional - create and assign collection box at once
- **Method:** POST
- **URL:** `/api/v1/collection-boxes/register-and-assign?fundraisingEventId=your-fundraising-event-id
- **Sample cURL:**
```bash
curl -X POST "http://localhost:8080/api/v1/collection-boxes/register-and-assign?fundraisingEventId=your-fundraising-event-id"
```

---

## Notes:
- Replace `your-collection-box-id` and `your-fundraising-event-id` with actual UUIDs.
- Make sure `Content-Type: application/json` is set for POST requests with JSON bodies.
- The database is in-memory (H2), so data will reset after each application restart.
- # There are only 3 currencies available USD, EUR, and PLN
- 

# TASK

1. Every collection box should have a unique identifier.
2. A collection box can be assigned to only one fundraising event at a time.
3. You can only assign a collection box to a fundraising event if the collection box is empty.
4. When a collection box is unregistered, it is automatically emptied. No money is transfered to any
fundraising event’s account.
5. A collection box can contain money in different currencies. For simplicity, you can limit possible
currencies to a subset of your choice (e.g. three different currencies).
6. Fundraising events have their own accounts where they store collected money. The account has
only one currency.
7. When money is transfered from a collection box to a fundraising event’s account, it is converted
to the currency used by the fundraising event. The exchange rates can be hardcoded.
Optional: Fetch currency exchange rates from an online API.'

REST API endpoints
The application should expose all REST endpoint defined below. For simplicity, the data input and
output format can be JSON.
1. Create a new fundraising event.
2. Register a new collection box.
3. List all collection boxes. Include information if the box is assigned (but don’t expose to what
fundraising event) and if it is empty or not (but don’t expose the actual value in the box).
4. Unregister (remove) a collection box (e.g. in case it was damaged or stolen).
5. Assign the collection box to an existing fundraising event.
6. Put (add) some money inside the collection box.
7. Empty the collection box i.e. “transfer” money from the box to the fundraising event’s account.
8. Display a financial report with all fundraising events and the sum of their accounts.

Non-functional requirements
1. Crete only the backend part (no UI is required).
2. The application should expose a REST API.
3. Use Java programming language and Spring Framework.
4. Use Maven or Gradle.
5. Use relational in-memory database (e.g. H2).
6. No security features (authorization, authentication, encryption etc.) are required.
