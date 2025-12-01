# User API

## Register User
Endpoint : POST /api/users

Request Body :
```json
{
  "username" : "Prabowo",
  "password" : "Rahasia",
  "name" : "Prabowo Subianto"
}

```
Response Body (success):
```json
{
  "data" : "success"
}
```
Response Body (Failed):
```json
{
  "errors" : "Username and password doesn't match"
}
```
## Login User
Endpoint : POST /api/auth/login

Request Body :
```json
{
  "username" : "Prabowo",
  "password" : "Rahasia"
}

```
Response Body (success):
```json
{
  "data" : {
    "token" : "TOKEN",
    "expiredAt" : 5635467254 //miliseconds
  }
}
```
Response Body (Failed):
```json
{
  "errors" : "Username and password doesn't match"
}
```
## Get User
Endpoint : GET /api/users/current

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (success):
```json
{
  "data" : {
    "username" : "Prabowo",
    "name" : "Prabowo Subianto"
  }
}
```
Response Body (Failed, 401):
```json
{
  "errors" : "Unauthorized"
}
```
## Update User
Endpoint : PATCH /api/users/current

Request Header :
- X-API-TOKEN : Token (Mandatory)

Request Body :
```json
{
  "name" : "Prabowo", //put if only want to update name
  "password" : "new password" //put if only want to update password
}
```
Response Body (success):
```json
{
  "data" : {
    "username" : "Prabowo", 
    "name" : "Prabowo Subianto" 
  }
}
```
Response Body (Failed, 401):
```json
{
  "errors" : "Unauthorized"
}
```

## Logout User
Endpoint : DELETE /api/auth/logout

Request Header :
- X-API-TOKEN : Token (Mandatory)

Response Body (Success):
```json
{
  "data" : "success"
}
```