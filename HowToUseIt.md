# Exchange Rate API - How to Use

This guide explains how to set up, run and test the Exchange Rate API.

---

## Set Environment Variables

Before running the app, define your environment variables.
These variables are automatically picked up by Spring Boot via application.properties.

### Windows CMD

```cmd
set EXCHANGE_RATE_API_KEY=your_api_key_here
set EXCHANGE_RATE_USERNAME=your_username_here
set EXCHANGE_RATE_PASSWORD=your_password_here
```

### Linux/macOS

```
export EXCHANGE_RATE_API_KEY=your_api_key_here
export EXCHANGE_RATE_USERNAME=your_username_here
export EXCHANGE_RATE_PASSWORD=your_password_here
```
You can also define them in your IDE run configuration.

---

## Run the Application

**Terminal:**

``mvn spring-boot:run``

**Intellij**:

- Go to Run → Edit Configurations → Environment variables
- Add the environment variables above
- Click Run

The API will start on: ```http://localhost:8080```

---

## Login to Get JWT Token

Use this one-line CMD curl command:
```curl -s -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d "{\"username\":\"username\",\"password\":\"password\"}"```

- Replace "username" and "password" with your environment variable values. The response will be a JWT token.

Store it in a CMD variable: ````set TOKEN=<paste-your-JWT-here>````


**You’ll use %TOKEN% in the Authorization header for all other requests.**

---

## Test Endpoints - examples Windows

### /exchange-rate

```cmd
Sucess (200)
curl -X POST http://localhost:8080/api/exchange-rate -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"fromCurrency\":\"USD\",\"toCurrency\":\"EUR\"}"

Bad Request (400)
curl -X POST http://localhost:8080/api/exchange-rate -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"fromCurrency\":null,\"toCurrency\":\"EUR\"}"
```

### /rates
```cmd
Success 
curl -X GET "http://localhost:8080/api/rates?currency=USD" -H "Authorization: Bearer %TOKEN%"

Bad Request (400)
curl -X GET "http://localhost:8080/api/rates?currency=" -H "Authorization: Bearer %TOKEN%"
```

### /convert
```cmd
Success (200)
curl -X POST "http://localhost:8080/api/convert?amount=100" -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"fromCurrency\":\"USD\",\"toCurrency\":\"EUR\"}"

Bad Request (400)
curl -X POST "http://localhost:8080/api/convert?amount=-10" -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN%" -d "{\"fromCurrency\":\"USD\",\"toCurrency\":\"EUR\"}"
```

### /convert-multiple
```cmd
Success (200)
curl -X GET "http://localhost:8080/api/convert-multiple?from=USD&targets=EUR&targets=GBP&amount=100" -H "Authorization: Bearer %TOKEN%"

Bad Request (400)
curl -X GET "http://localhost:8080/api/convert-multiple?from=&targets=EUR&amount=100" -H "Authorization: Bearer %TOKEN%"
```

## Test Endpoints with Postman

1. Login to get JWT

- Method: POST
- URL: http://localhost:8080/api/login
- Body → raw JSON:
```json
{
    "username": "username",
    "password": "password"
}
```
- copy the JWT token from the response.

2. Set Authorization for other requests

- Go to Authorization → Type: Bearer Token
- Paste the JWT token you got from login.

3. Test other endpoints

**POST /api/exchange-rate**
- Body JSON:

```json
{
"fromCurrency": "USD",
"toCurrency": "EUR"
}
```

**GET /api/rates?currency=USD**

**POST /api/convert?amount=100 → Body JSON same as /exchange-rate**

**GET /api/convert-multiple?from=USD&targets=EUR&targets=GBP&amount=100**