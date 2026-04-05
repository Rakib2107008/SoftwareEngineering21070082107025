# Testing Details: Service Tests and Integration Tests

This file explains:
1. Why service tests and integration tests are used in this project.
2. What each method in all service and integration test files does.

## 1) Why These Two Test Layers Are Used

### Service Tests (Unit Tests)
Service tests validate business logic in isolation.

In your project, service tests:
- Mock repositories and external collaborators.
- Check business rules quickly (no full Spring context required).
- Focus on method behavior, exceptions, and transformations.

Benefits:
- Fast feedback.
- Easy to pinpoint logic defects.
- Low setup overhead.

### Integration Tests (Controller Slice Tests)
Integration tests validate HTTP/controller behavior and request/response contracts.

In your project, integration tests:
- Use MockMvc with `@WebMvcTest` to load specific controller slices.
- Mock service layer dependencies.
- Verify endpoint status codes and JSON payload fields.

Benefits:
- Ensures controllers are correctly wired.
- Confirms request mapping and serialization/deserialization.
- Validates API contract shape expected by frontend clients.

Note:
- Current integration tests use `@AutoConfigureMockMvc(addFilters = false)`, so security filter chain behavior is intentionally bypassed for controller contract testing.

---

## 2) Service Test Files and Methods

## File: src/test/java/com/mobilezbd/service/UserServiceTest.java

### Method: setUp()
Purpose:
- Creates `UserService` with mocked dependencies.
- Injects `adminEmail` and `adminPassword` using reflection helper.

Why used:
- Ensures each test starts with a fresh, controlled service instance.

### Method: testRegister_success()
Purpose:
- Tests successful customer registration flow.

What it does:
- Creates a valid `RegisterRequest`.
- Mocks `existsByEmail` as false.
- Mocks password hashing and user/account persistence.
- Mocks user details loading and JWT generation.
- Asserts returned token is `token`.

Expected behavior verified:
- Register returns an auth response with generated JWT token.

### Method: testRegister_sellerRole()
Purpose:
- Tests successful seller registration and role mapping.

What it does:
- Sends role `SELLER` in request.
- Mocks persistence and JWT generation for seller user details.
- Asserts returned role is `ROLE_SELLER`.

Expected behavior verified:
- Role translation and response role value are correct for seller registration.

### Method: testRegister_duplicateEmail()
Purpose:
- Tests duplicate email rejection.

What it does:
- Mocks `existsByEmail` as true.
- Asserts `BusinessException` is thrown.

Expected behavior verified:
- Duplicate registration is blocked.

### Method: testLogin_validCredentials()
Purpose:
- Tests successful login token response.

What it does:
- Creates valid auth request.
- Mocks user lookup and user details.
- Mocks token generation.
- Asserts returned token is `ok`.

Expected behavior verified:
- Login success path returns JWT token.

### Method: testLogin_invalidPassword()
Purpose:
- Tests failed login due to bad credentials.

What it does:
- Mocks `authenticationManager.authenticate(...)` to throw `BadCredentialsException`.
- Asserts exception is thrown from service login.

Expected behavior verified:
- Invalid credentials are rejected.

### Method: setField(Object target, String fieldName, Object value)
Purpose:
- Reflection helper to set private fields in `UserService` during test setup.

Why used:
- Makes internal config values deterministic in unit tests.

---

## File: src/test/java/com/mobilezbd/service/ProductServiceTest.java

### Method: setUp()
Purpose:
- Creates `ProductService` with mocked `ProductDetailsRepository`.

### Method: testGetProductById_found()
Purpose:
- Tests fetching existing product by id.

What it does:
- Mocks repository `findById(1)` to return product.
- Asserts returned product name is `iPhone`.

### Method: testGetProductById_notFound()
Purpose:
- Tests non-existent product lookup.

What it does:
- Mocks empty optional for id 99.
- Asserts `ResourceNotFoundException`.

### Method: testAddProduct_success()
Purpose:
- Tests add-product behavior.

What it does:
- Builds request using helper method.
- Mocks save call.
- Asserts saved product name is `Phone X`.

### Method: testUpdateProduct_success()
Purpose:
- Tests update behavior for existing product.

What it does:
- Mocks existing product lookup and save.
- Asserts updated name is `Phone X`.

### Method: testDeleteProduct_success()
Purpose:
- Tests delete flow for existing id.

What it does:
- Mocks `existsById` true.
- Calls delete and verifies `deleteById` was called.

### Method: testGetByCategory_returnsFiltered()
Purpose:
- Tests category filtering.

What it does:
- Mocks repository category query result.
- Asserts result list size is 1.

### Method: request()
Purpose:
- Shared test data factory for `AdminProductRequest`.

Why used:
- Avoids duplicate fixture construction across add/update tests.

---

## File: src/test/java/com/mobilezbd/service/OrderServiceTest.java

### Method: setUp()
Purpose:
- Creates `OrderService` with mocked `CustomerInfoService` and `ProductSellHistoryRepository`.

### Method: testCreateOrder_success()
Purpose:
- Tests successful order creation orchestration.

What it does:
- Mocks dependency `placeOrder(...)` response.
- Calls `createOrder(...)` and asserts customer name.

### Method: testCreateOrder_stockShortage()
Purpose:
- Tests failure path when stock patch fails.

What it does:
- Mocks `customerInfoService.patchStock(...)` to throw `BusinessException`.
- Asserts `createOrder(...)` throws exception.

### Method: testGetOrdersByCustomer()
Purpose:
- Tests retrieval of order history by customer email.

What it does:
- Mocks sell-history repository result.
- Asserts list size is 1.

### Method: request()
Purpose:
- Builds a sample `CheckoutRequest` with customer + item.

Why used:
- Reusable fixture for create-order tests.

---

## File: src/test/java/com/mobilezbd/service/CartServiceTest.java

Note:
- This test class is named `CartServiceTest` but tests `CartListService` behavior.

### Method: setUp()
Purpose:
- Creates `CartListService` with mocked product repository.

### Method: testValidateCart_sufficient()
Purpose:
- Tests valid cart when stock is enough.

What it does:
- Mocks product quantity = 10.
- Creates cart item quantity = 2.
- Asserts validation result is valid.

### Method: testValidateCart_insufficient()
Purpose:
- Tests invalid cart when requested quantity exceeds stock.

What it does:
- Mocks product quantity = 1.
- Requests quantity = 3.
- Asserts validation result is invalid.

### Method: product(int qty)
Purpose:
- Helper factory to build `ProductDetails` with configurable stock quantity.

---

## 3) Integration Test Files and Methods

## File: src/test/java/com/mobilezbd/integration/AuthControllerTest.java

Configuration in file:
- `@WebMvcTest(AuthController.class)`: loads auth controller slice.
- `@AutoConfigureMockMvc(addFilters = false)`: disables security filters for this test slice.

### Method: registerLoginAndAccessProtectedEndpoint()
Purpose:
- Verifies auth controller HTTP contract for register/login.

What it does:
- Mocks `userService.register(...)` and `userService.login(...)` to return same `AuthResponse`.
- Sends POST `/api/auth/register` with JSON body.
- Asserts `201 Created` and token field.
- Sends POST `/api/auth/login` with JSON body.
- Asserts `200 OK` and token field.
- Sends POST login again with Authorization header.
- Asserts status OK.

Expected behavior verified:
- Auth endpoints accept expected request body and return expected status/JSON shape.

---

## File: src/test/java/com/mobilezbd/integration/ProductControllerTest.java

Configuration in file:
- Loads `AdminController` slice using `@WebMvcTest(AdminController.class)`.
- Filters disabled with `addFilters = false`.

Naming note:
- Class name says ProductControllerTest but it is testing AdminController product endpoints.

### Method: adminCrudFlowWithJwt()
Purpose:
- Verifies admin product CRUD endpoint contracts.

What it does:
- Mocks admin service add/update/delete/list operations.
- Creates `AdminProductRequest` payload.
- Sends POST `/api/admin/products` and expects created + product name.
- Sends PUT `/api/admin/products/1` and expects OK.
- Sends GET `/api/admin/products` and expects OK.
- Sends DELETE `/api/admin/products/1` and expects no-content.

Expected behavior verified:
- Admin product routes respond with expected status codes and payload field(s).

---

## File: src/test/java/com/mobilezbd/integration/OrderControllerTest.java

Configuration in file:
- Loads `CustomerInfoController` slice.
- Filters disabled with `addFilters = false`.

### Method: placeOrderFlowAsCustomer()
Purpose:
- Verifies order creation endpoint contract for customer checkout flow.

What it does:
- Mocks `orderService.createOrder(...)` to return a populated `OrderResponse`.
- Builds nested request payload containing customer info and item list.
- Sends POST `/api/orders` with principal and auth header.
- Asserts status OK and `customerName` in JSON response.

Expected behavior verified:
- Endpoint can parse request payload and return expected response fields.

---

## File: src/test/java/com/mobilezbd/integration/SellerControllerTest.java

Configuration in file:
- Loads `SellerController` slice.
- Filters disabled with `addFilters = false`.

### Method: sellerCrudAndProfileFlow()
Purpose:
- Verifies seller-side endpoint contracts across products, profile, and orders.

What it does:
- Mocks seller service for add/get/update/delete own products.
- Mocks user service for seller profile.
- Mocks order service for seller order list.
- Builds request payload for product operations.
- Executes:
  - POST `/api/seller/products` (expects created + name)
  - GET `/api/seller/products` (expects OK)
  - PUT `/api/seller/products/5` (expects OK)
  - DELETE `/api/seller/products/5` (expects no-content)
  - GET `/api/seller/profile` (expects role field)
  - GET `/api/seller/orders` (expects first item product name)

Expected behavior verified:
- Seller controller routes return correct status codes and key response fields.

---

## 4) Practical Summary

Service tests are used here to validate business logic and edge cases quickly through mock-driven unit testing.
Integration tests are used here to validate endpoint contracts (path, payload, status, JSON fields) with MockMvc controller slices.

Together, these layers reduce regressions by covering both:
- internal logic correctness (service layer), and
- external API behavior correctness (controller layer).
