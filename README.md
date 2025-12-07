Customer Ordered Products API

Short description

This service accepts a customer name and returns a list of products that the customer has ordered. The application integrates with three external mock APIs (Customer, Orders, Products) exposed by a json-server instance. The README documents how to run the mock server and the Spring Boot application, the API contract, the implementation logic, example requests/responses, error handling, and testing steps.

Prerequisites

- Java 17+ (or the JDK version used by the project)
- Maven (the repository includes the Maven wrapper `mvnw.cmd` for Windows)
- Node.js (for running json-server via `npx`)
- PowerShell (Windows examples below use PowerShell)

Project files of interest

- Controller: src/main/java/com/example/sunilkumarl/demo/controller/CustomerProductController.java
- Facade: src/main/java/com/example/sunilkumarl/demo/service/CustomerOrdersProductFacade.java
- Mock data and json-server instruction: src/main/resources/ecom_mock_data/db.json and src/main/resources/ecom_mock_data/api-start-json-server.txt
- Application properties: src/main/resources/application.properties

Start the external mock APIs (json-server)

The repository includes a small mock data set under `src/main/resources/ecom_mock_data`. The instructions in `api-start-json-server.txt` demonstrate how the json-server is started. From the project root, run (PowerShell):

```powershell
cd .\src\main\resources\ecom_mock_data
npx json-server db.json --port 3000
```

This starts a mock HTTP server on http://localhost:3000 that exposes the following endpoints (based on db.json):

- http://localhost:3000/customers
- http://localhost:3000/orders
- http://localhost:3000/products
- http://localhost:3000/prices

(The `api-start-json-server.txt` file included in the repo shows the same command and sample output.)

Run the Spring Boot application (Windows)

From the project root run (PowerShell):

```powershell
# Use the Maven wrapper included with the repo
.\mvnw.cmd spring-boot:run
```

By default the application runs on http://localhost:8080. If you need to change the external mock server URL, update `application.properties` or set an environment variable for the external services base URL (e.g. `external.service.base-url=http://localhost:3000`).

API Contract

- Endpoint: GET /customer-products
- Query parameter: customerName (required) — the customer's full name (URL encoded as needed)
- Response: 200 OK — JSON array of Product objects. Each Product object contains the fields (as used in the project `Product` model):
  - product_id (number)
  - product_desc (string)
  - product_image_url (string)
  - product_category (string)

Examples

1) Customer API (external mock json-server)

Request (example):

http://localhost:3000/customers?customer_name=Bob%20Smith

Sample response:

```json
[
  {
    "customer_id": 2,
    "customer_name": "Bob Smith",
    "email": "bob.smith@example.com",
    "phone_number": "9123456780",
    "id": "22ba"
  }
]
```

2) Orders API (external mock json-server)

Request (example):

http://localhost:3000/orders?customer_id=2

Sample response:

```json
[
  {
    "order_id": 1003,
    "order_datetime": "2025-11-03T09:45:00",
    "customer_id": 2,
    "order_total": 240,
    "order_lines": [
      {
        "order_id": 1003,
        "order_line_id": 1,
        "order_line_seq_id": 1,
        "product_id": 7,
        "product_price": 120,
        "product_qty": 2
      }
    ],
    "id": "88dc"
  },
  {
    "order_id": 1004,
    "order_datetime": "2025-11-03T16:30:00",
    "customer_id": 2,
    "order_total": 199.99,
    "order_lines": [
      {
        "order_id": 1004,
        "order_line_id": 1,
        "order_line_seq_id": 1,
        "product_id": 9,
        "product_price": 199.99,
        "product_qty": 1
      }
    ],
    "id": "c5d1"
  }
]
```

3) Products API (external mock json-server)

Request (example):

http://localhost:3000/products

Sample response:

```json
[
  {
    "product_id": 1,
    "product_desc": "Wireless Mouse",
    "product_image_url": "http://example.com/mouse.jpg",
    "product_category": "Electronics",
    "id": "f089"
  },
  {
    "product_id": 2,
    "product_desc": "Mechanical Keyboard",
    "product_image_url": "http://example.com/keyboard.jpg",
    "product_category": "Electronics",
    "id": "076e"
  },
  {
    "product_id": 3,
    "product_desc": "Gaming Headset",
    "product_image_url": "http://example.com/headset.jpg",
    "product_category": "Electronics",
    "id": "f007"
  }
]
```

How the application works (implementation logic)

1. The controller receives a request to GET /customer-products?customerName={name}.
2. The application calls the Customer API: GET /customers?customer_name={customer_name} and expects an array of matching customers. The implementation picks the first matching customer (by index 0) and reads `customer_id`.
3. With the `customer_id`, the application calls the Orders API: GET /orders?customer_id={customer_id} and collects the customer's orders.
4. From all returned orders, the application extracts all `product_id` values from each order's `order_lines` array.
5. The application calls the Products API: GET /products and receives the full product catalog, then filters the catalog to only include products whose `product_id` matches the collected IDs.
6. The filtered product list is returned to the caller as the API response.

Notes about the implementation

- If multiple customers are returned by the Customers API for the given name, the current implementation uses the first item.
- Duplicate product IDs (across multiple orders) are typically deduplicated by the filtering step (implementation may collect IDs into a Set).
- The app relies on the mock json-server endpoints; ensure the mock server is running before calling the app.

Final API example

Once the json-server and the Spring Boot application are up:

Request (example):

http://localhost:8080/customer-products?customerName=Charlie%20Brown

Response (example):

```json
[
  {
    "product_id": 17,
    "product_category": "Fashion",
    "product_image_url": "http://example.com/sunglasses.jpg",
    "product_desc": "Sunglasses"
  },
  {
    "product_id": 1,
    "product_category": "Electronics",
    "product_image_url": "http://example.com/mouse.jpg",
    "product_desc": "Wireless Mouse"
  },
  {
    "product_id": 10,
    "product_category": "Electronics",
    "product_image_url": "http://example.com/monitor.jpg",
    "product_desc": "LED Monitor"
  },
  {
    "product_id": 12,
    "product_category": "Home",
    "product_image_url": "http://example.com/lamp.jpg",
    "product_desc": "Desk Lamp"
  }
]
```

Error handling and status codes

- 400 Bad Request: missing required parameter `customerName`.
- 404 Not Found: customer not found (Customers API returned an empty array).
- 200 OK with []: customer exists but has no orders, or no products match the ordered product IDs.
- 502 Bad Gateway (or 502-like): external mock API unreachable or returns an unexpected error — the app should log upstream error details and return a suitable 5xx response.
- 500 Internal Server Error: unexpected runtime error in the application.

Testing

Manual testing (PowerShell examples):

1) Start mock server (in a new terminal):

```powershell
cd .\src\main\resources\ecom_mock_data
npx json-server db.json --port 3000
```

2) Start the Spring Boot application (in another terminal):

```powershell
cd C:\Users\sunil\my_apps\sb-customer-orders-products
.\mvnw.cmd spring-boot:run
```

3) Test the endpoint with PowerShell's Invoke-RestMethod:

```powershell
Invoke-RestMethod -Method GET -Uri 'http://localhost:8080/customer-products?customerName=Charlie%20Brown'
```

Or using curl (if installed):

```powershell
curl "http://localhost:8080/customer-products?customerName=Charlie%20Brown"
```

Suggested automated tests

- Unit tests for CustomerService, OrderService, ProductService using mocked HTTP clients to simulate json-server responses.
- Integration test that starts the application (and optionally the mock server) and asserts the returned product list for a known customer.

Likely edge cases

- Multiple customers with same name — currently the first returned customer is used.
- Names with special characters — ensure URL encoding when calling the API.
- Orders with missing or empty `order_lines` or malformed entries.
- Product IDs referenced in orders but missing from the Products API.
- Large number of orders/product ids — may need paging/streaming for production.

Configuration

- Default mock server base URL: http://localhost:3000 (update `application.properties` if different).
- Application port: 8080 by default (change via `server.port` property).


