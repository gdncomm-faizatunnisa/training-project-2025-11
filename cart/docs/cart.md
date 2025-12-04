# Cart Service API Specification

## 1. Get Cart
Retrieves the current state of the user's cart.

**URL:** `/api/carts/{userId}`
**Method:** `GET`

### Response
**Status:** `200 OK`

```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 101,
      "productName": null,
      "quantity": 2,
      "price": 15000.00,
      "subtotal": 30000.00
    }
  ],
  "total": 30000.00
}
```

## 2. Add Item to Cart
Adds a product to the cart or updates the quantity if it already exists.

**URL:** `/api/carts/{userId}/items`
**Method:** `POST`

### Request Body
```json
{
  "productId": 101,
  "quantity": 2
}
```

### Response
**Status:** `200 OK`

```json
{
  "id": 1,
  "userId": 1,
  "items": [
    {
      "id": 1,
      "productId": 101,
      "productName": null,
      "quantity": 2,
      "price": 15000.00,
      "subtotal": 30000.00
    }
  ],
  "total": 30000.00
}
```

## 3. Remove Item from Cart
Removes a specific product from the cart.

**URL:** `/api/carts/{userId}/items/{productId}`
**Method:** `DELETE`

### Response
**Status:** `200 OK`

```json
{
  "id": 1,
  "userId": 1,
  "items": [],
  "total": 0.00
}
```

## 4. Clear Cart
Removes all items from the cart.

**URL:** `/api/carts/{userId}`
**Method:** `DELETE`

### Response
**Status:** `204 No Content`
