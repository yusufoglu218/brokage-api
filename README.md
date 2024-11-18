# Brokerage System - Backend API

This backend api is designed for a brokerage firm to manage stock orders and customer finances. The service allows employees to interact with customer orders, including creating, listing, and canceling orders. It also supports deposit and withdrawal operations for customer funds. Additionally, an admin interface is provided to  match pending transactions and handle customer assets.

## Key Features:
- **Order Management**: Create, list, and cancel orders for customers.
- **Deposit and Withdrawal**: Deposit and withdraw funds for customers in `TRY` (Turkish Lira).
- **Admin Features**: Admin users can match pending orders.
- **Authentication**: All endpoints are authenticated with basic authentication.
- **Transactional Integrity**: Ensures admin transactions are atomic and consistent, especially in asset handling and order matching.

## Technologies Used:
- **Java 17**
- **Spring Boot 3.3.5**
- **H2 Database**
- **MapStruct for DTO mapping**
- **Lombok for boilerplate code**
- **Swagger for API documentation**

## API Endpoints:

All endpoints are accessible with only the basic authentication (admin user and password)

### Order Management
- **POST /api/orders**
    - Description: Create a new order for a given customer.
    - Request Body:
      ```json
      {
        "customerId": "123",
        "assetName": "AAPL",
        "orderSide": "BUY",
        "size": 10,
        "price": 150.50
      }
      ```
    
- **GET /api/orders**
    - Description: List orders for a given customer and date range (optional filters).
    - Query Parameters:
        - `customerId`: (Optional) Filter by customer ID.
        - `startDate`: (Optional) Filter by start date.
        - `endDate`: (Optional) Filter by end date.


- **POST /api/orders/{orderId}/cancel**
    - Description: Cancel a pending order. Only orders with `PENDING` status can be canceled.
    - Path Parameter:
        - `orderId`: The ID of the order to cancel.

### Deposit and Withdraw Operations
- **POST /api/assets/deposit**
    - Description: Deposit a certain amount of `TRY` for a given customer.
    - Request Body:
      ```json
      {
        "customerId": "123",
        "amount": 1000
      }
      ```

- **POST /api/assets/withdraw**
    - Description: Withdraw a certain amount of `TRY` for a given customer.
    - Request Body:
      ```json
      {
        "customerId": "123",
        "amount": 500
      }
      ```

### Asset Management
- **GET /api/assets**
    - Description: List all assets for a given customer, including `TRY` as an asset.
    - Query Parameters:
        - `customerId`: (Required) The customer ID.

### Admin Operations
- **POST /api/v1/admin/matchOrders**
    - Description: Match all pending orders


### Monitor and check

|HTTP Method|URL|Description|
|---|---|---|
|`GET`|http://localhost:8080/ | Root page |
|`GET`|http://localhost:8080/swagger-ui/index.html | Swagger UI page |
|`GET`|http://localhost:8080/actuator | Actuator page |

### H2 Database Console Endpoint
|`GET`|http://localhost:8080/h2-console| H2 database console page |
    
## Database Schema

### Asset Table

| Column        | Type       | Description                         |
|---------------|------------|-------------------------------------|
| `id`          | Long       | The asset ID.                   |
| `customerId`  | Long       | The customer ID.                    |
| `assetName`   | String     | The name of the asset (e.g., `AAPL`, `TRY`). |
| `size`        | BigDecimal | Total amount/quantity of the asset. |
| `usableSize`  | BigDecimal | The available amount of the asset for transactions. |

### Order Table

| Column        | Type       | Description                         |
|---------------|------------|-------------------------------------|
| `id`          | Long       | The ID of the order.               |
| `customerId`  | Long       | The ID of the customer placing the order. |
| `assetName`   | String     | The asset being bought or sold.     |
| `orderSide`   | Enum       | The side of the order: `BUY` or `SELL`. |
| `size`        | Long       | The number of units of the asset.   |
| `price`       | BigDecimal | The price per unit of the asset.    |
| `status`      | Enum       | The status of the order: `PENDING`, `MATCHED`, or `CANCELED`. |
| `createDate`  | Date       | The date the order was created.     |


### Customer Table

| Column        | Type       | Description                         |
|---------------|------------|-------------------------------------|
| `id`          | Long       | The customer ID.                    |
| `name`        | String       | The customer name.                  |
| `surname`     | String     | The customer surname.               |
| `email`       | String     | The customer email                  |
---

## Build and Run application

#### Dev Environment

> **```mvn clean install -Pdev```** to build project
>
> **```mvn spring-boot:run -Dspring-boot.run.profiles=dev```** to run the project

#### Prod Environment

> **```mvn clean install -Pprod```** to build project
>
> **```mvn spring-boot:run -Dspring-boot.run.profiles=prod```** to run the project
>
>
#### Docker build

> **```docker build -t brokerage-service .```** to build project
>
> **```docker run -p 8080:8080 brokerage-service```** to run the project
>

