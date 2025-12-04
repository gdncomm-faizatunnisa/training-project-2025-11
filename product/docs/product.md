# Product API Specification

## Overview
This document defines the REST API endpoints for the Product Service, which provides CRUD operations for products, along with search and listing capabilities with pagination support.

## Base URL
```
/api/products
```

## Data Models

### Product (API Response)
```json
{
  "id": "long",
  "sku": "string",
  "name": "string",
  "description": "string",
  "price": "number",
  "stock": "integer"
}
```

### Database Model (MySQL)
```sql
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sku VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  price DOUBLE NOT NULL,
  stock INT NOT NULL
);
```

### Paginated Response
```json
{
  "content": [Product],
  "page": "integer",
  "size": "integer",
  "totalElements": "long",
  "totalPages": "integer",
  "last": "boolean"
}
```

### Error Response
```json
{
  "timestamp": "string (ISO 8601)",
  "status": "integer",
  "error": "string",
  "message": "string",
  "path": "string"
}
```

---

## API Endpoints

### 1. Create Product

**Endpoint:** `POST /api/products`

**Description:** Creates a new product.

**Request Body:**
```json
{
  "sku": "string (required, unique)",
  "name": "string (required, not blank)",
  "description": "string (optional, max 1000 characters)",
  "price": "number (required, minimum 0.0)",
  "stock": "integer (required, minimum 0)"
}
```

**Response:**
- **Status Code:** `201 Created`
- **Body:**
```json
{
  "id": 1,
  "sku": "LAP-001",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input data (validation errors)

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "LAP-001",
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stock": 50
  }'
```

---

### 2. Get Product by ID

**Endpoint:** `GET /api/products/{id}`

**Description:** Retrieves a single product by its ID.

**Path Parameters:**
- `id` (string, required) - Product ID (ObjectId)

**Response:**
- **Status Code:** `200 OK`
- **Body:**
```json
{
  "id": 1,
  "sku": "LAP-001",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "stock": 50
}
```

**Error Responses:**
- `404 Not Found` - Product with the specified ID does not exist

**Example Request:**
```bash
curl -X GET http://localhost:8080/api/products/1
```

---

### 3. Get All Products (Paginated)

**Endpoint:** `GET /api/products`

**Description:** Retrieves a paginated list of all products.

**Query Parameters:**
- `page` (integer, optional, default: 0) - Page number (zero-indexed)
- `size` (integer, optional, default: 20) - Number of items per page
- `sort` (string, optional, default: "sku,asc") - Sort criteria in the format: `property,direction` (e.g., "name,desc", "price,asc", "sku,asc")

**Response:**
- **Status Code:** `200 OK`
- **Body:**
```json
{
  "content": [
    {
      "id": 1,
      "sku": "LAP-001",
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99,
      "stock": 50
    },
    {
      "id": 2,
      "sku": "MOU-001",
      "name": "Mouse",
      "description": "Wireless mouse",
      "price": 29.99,
      "stock": 100
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1,
  "last": true
}
```

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=10&sort=name,asc"
```

---

### 4. Search Products (Paginated)

**Endpoint:** `GET /api/products/search`

**Description:** Searches for products by name with wildcard support. Returns paginated results.

**Query Parameters:**
- `query` (string, required) - Search query (supports wildcard matching)
- `page` (integer, optional, default: 0) - Page number (zero-indexed)
- `size` (integer, optional, default: 20) - Number of items per page
- `sort` (string, optional, default: "sku,asc") - Sort criteria in the format: `property,direction`

**Wildcard Search:**
- The search supports partial matching (e.g., "lap" will match "Laptop")
- Case-insensitive search

**Response:**
- **Status Code:** `200 OK`
- **Body:**
```json
{
  "content": [
    {
      "id": 1,
      "sku": "LAP-001",
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99,
      "stock": 50
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

**Example Requests:**
```bash
# Search for products containing "lap"
curl -X GET "http://localhost:8080/api/products/search?query=lap&page=0&size=10"

# Search for products containing "mouse" sorted by price
curl -X GET "http://localhost:8080/api/products/search?query=mouse&page=0&size=10&sort=price,asc"
```

---

### 5. Update Product

**Endpoint:** `PUT /api/products/{id}`

**Description:** Updates an existing product.

**Path Parameters:**
- `id` (string, required) - Product ID (ObjectId)

**Request Body:**
```json
{
  "sku": "string (required, unique)",
  "name": "string (required, not blank)",
  "description": "string (optional, max 1000 characters)",
  "price": "number (required, minimum 0.0)",
  "stock": "integer (required, minimum 0)"
}
```

**Response:**
- **Status Code:** `200 OK`
- **Body:**
```json
{
  "id": 1,
  "sku": "LAP-001",
  "name": "Gaming Laptop",
  "description": "High-performance gaming laptop",
  "price": 1299.99,
  "stock": 30
}
```

**Error Responses:**
- `400 Bad Request` - Invalid input data (validation errors)
- `404 Not Found` - Product with the specified ID does not exist

**Example Request:**
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "LAP-001",
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop",
    "price": 1299.99,
    "stock": 30
  }'
```

---

### 6. Delete Product

**Endpoint:** `DELETE /api/products/{id}`

**Description:** Deletes a product by its ID.

**Path Parameters:**
- `id` (string, required) - Product ID (ObjectId)

**Response:**
- **Status Code:** `204 No Content`

**Error Responses:**
- `404 Not Found` - Product with the specified ID does not exist

**Example Request:**
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

---

## Validation Rules

### Product Fields
- **sku**: Required, must be unique across all products
- **name**: Required, cannot be blank
- **description**: Optional, maximum 1000 characters
- **price**: Required, must be >= 0.0
- **stock**: Required, integer valued, must be >= 0

---

## HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 OK | Request successful |
| 201 Created | Resource created successfully |
| 204 No Content | Resource deleted successfully |
| 400 Bad Request | Invalid request data or validation error |
| 404 Not Found | Resource not found |
| 500 Internal Server Error | Server error |

---

## Pagination Details

### Request Parameters
- **page**: Zero-indexed page number (default: 0)
- **size**: Number of items per page (default: 20, max: 100)
- **sort**: Sorting criteria in format `property,direction` (e.g., "name,asc", "price,desc")

### Response Fields
- **content**: Array of product objects for the current page
- **page**: Current page number (zero-indexed)
- **size**: Number of items per page
- **totalElements**: Total number of items across all pages
- **totalPages**: Total number of pages
- **last**: Boolean indicating if this is the last page

---

## Examples

### Example 1: Create and Retrieve a Product
```bash
# Create a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "KEY-001",
    "name": "Wireless Keyboard",
    "description": "Ergonomic wireless keyboard",
    "price": 79.99,
    "stock": 75
  }'

# Response: {"id": 3, "sku": "KEY-001", "name": "Wireless Keyboard", ...}

# Retrieve the product
curl -X GET http://localhost:8080/api/products/3
```

### Example 2: Search and Paginate
```bash
# Search for "wireless" products, get second page with 5 items per page
curl -X GET "http://localhost:8080/api/products/search?query=wireless&page=1&size=5&sort=price,asc"
```

### Example 3: Update Product Stock
```bash
# Update product stock
curl -X PUT http://localhost:8080/api/products/3 \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "KEY-001",
    "name": "Wireless Keyboard",
    "description": "Ergonomic wireless keyboard",
    "price": 79.99,
    "stock": 50
  }'
```
