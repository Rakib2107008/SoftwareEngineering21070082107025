
# MobileZBD Security Details

This file documents the security model used in this project and explains all security-related classes/functions.

## 1) What Authentication and Authorization Type Is Used?

## Authentication Type
- Stateless token-based authentication using JWT (JSON Web Token).
- Client sends token as Bearer token in `Authorization` header.
- Backend validates token on each request using a custom filter.

## Authorization Type
- Role-Based Access Control (RBAC).
- Roles in system:
  - `ROLE_ADMIN`
  - `ROLE_CUSTOMER`
  - `ROLE_SELLER`
- Access is enforced through:
  - URL-level rules in Spring Security config.
  - Method-level rules with `@PreAuthorize`.

## Password Security
- Password hashing with BCrypt (`BCryptPasswordEncoder`).
- Raw passwords are not stored in DB.

## Session Strategy
- Stateless API security (`SessionCreationPolicy.STATELESS`).
- No server-side HTTP session used for authenticated user state.

---

## 2) Security-Related Backend Classes and Functions

## File: src/main/java/com/mobilezbd/security/SecurityConfig.java

Purpose:
- Main Spring Security configuration class.
- Defines authentication provider, authorization rules, filter chain, and CORS behavior.

### Class-level
- `@Configuration`: registers this as Spring config class.
- `@EnableMethodSecurity`: enables annotations like `@PreAuthorize`.

### Constructor
- `SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsServiceImpl userDetailsService)`
- Purpose: injects custom JWT filter and user details service.

### Function: filterChain(HttpSecurity http)
Purpose:
- Builds the security filter chain.

What it configures:
- Disables CSRF.
- Enables CORS with configured source.
- Sets stateless session policy.
- Defines route authorization:
  - Public web/static and some frontend routes are `permitAll`.
  - Public API routes: `/api/auth/**`, `/api/home/**`, `/api/products/**`, `/api/cart/**`, `/api/product/**`.
  - Role-protected API routes:
    - `/api/admin/**` -> ADMIN
    - `/api/customer/**` -> CUSTOMER
    - `/api/seller/**` -> SELLER
  - Any other route requires authentication.
- Registers DAO authentication provider.
- Adds `JwtAuthenticationFilter` before default username/password filter.

### Function: authenticationProvider()
Purpose:
- Creates `DaoAuthenticationProvider`.
- Connects `UserDetailsServiceImpl` + BCrypt password encoder.

### Function: authenticationManager(AuthenticationConfiguration config)
Purpose:
- Exposes `AuthenticationManager` bean from Spring config.
- Used by login flow in `UserService`.

### Function: passwordEncoder()
Purpose:
- Creates BCrypt encoder bean for password hashing.

### Function: corsConfigurationSource()
Purpose:
- Defines CORS policy.

Current behavior:
- Allows localhost origins with any port.
- Allows methods: GET, POST, PUT, PATCH, DELETE, OPTIONS.
- Allows all headers.
- Allows credentials.

---

## File: src/main/java/com/mobilezbd/security/JwtUtil.java

Purpose:
- Utility class for JWT generation and validation.

Fields:
- `secret`: loaded from `app.jwt.secret`.
- `expirationMs`: loaded from `app.jwt.expiration-ms`.

### Function: generateToken(UserDetails userDetails)
Purpose:
- Creates signed JWT containing:
  - subject = username (email)
  - issuedAt
  - expiration
- Signs using HMAC key from configured secret.

### Function: extractUsername(String token)
Purpose:
- Returns JWT subject (username/email).

### Function: extractExpiration(String token)
Purpose:
- Returns expiration date from token.

### Function: extractClaim(String token, Function<Claims, T> claimsResolver)
Purpose:
- Generic claim extractor.
- Parses token with signing key and resolves requested claim.

### Function: isTokenValid(String token, UserDetails userDetails)
Purpose:
- Validates token by checking:
  - token username matches provided userDetails username
  - token is not expired

### Function: isTokenExpired(String token)
Purpose:
- Checks if expiration is before current time.

### Function: getSigningKey()
Purpose:
- Builds HMAC secret key from configured secret string.

---

## File: src/main/java/com/mobilezbd/security/JwtAuthenticationFilter.java

Purpose:
- Runs once per request.
- Extracts and validates JWT from `Authorization` header.
- If valid, sets authenticated principal in `SecurityContext`.

### Constructor
- `JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService)`
- Injects JWT utility and user details loader.

### Function: doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
Purpose:
- Main authentication logic per request.

Flow:
1. Read `Authorization` header.
2. If missing or not `Bearer ...`, continue chain without auth.
3. Extract JWT token and username.
4. If username exists and no auth already set:
   - load user details
   - validate token
   - create `UsernamePasswordAuthenticationToken`
   - attach request details
   - set authentication into `SecurityContextHolder`.
5. On token parsing/validation error:
   - clear security context.
6. Continue filter chain.

---

## File: src/main/java/com/mobilezbd/security/UserDetailsServiceImpl.java

Purpose:
- Bridges application `User` entity to Spring Security `UserDetails`.

### Constructor
- `UserDetailsServiceImpl(UserRepository userRepository)`
- Injects user repository.

### Function: loadUserByUsername(String username)
Purpose:
- Finds user by email.
- Throws `UsernameNotFoundException` if user not found.
- Converts DB user to Spring Security user object with:
  - username = email
  - password = hashed password
  - authorities = role from DB (e.g. `ROLE_ADMIN`)

---

## File: src/main/java/com/mobilezbd/service/UserService.java

Why security-related:
- Handles register/login security workflow.
- Uses password encoding, authentication manager, and JWT creation.

### Constructor
- Injects:
  - `PasswordEncoder`
  - `AuthenticationManager`
  - `UserDetailsService`
  - `JwtUtil`
  - Repositories

### Function: register(RegisterRequest request)
Purpose:
- Registers a user securely.

Security behavior:
- Prevents duplicate emails.
- Parses and validates role through `parseRegistrationRole`.
- Hashes password via BCrypt encoder.
- Saves user and account.
- Loads principal and returns JWT token in `AuthResponse`.

### Function: parseRegistrationRole(String roleText)
Purpose:
- Normalizes and validates incoming registration role.

Rules:
- Default role = CUSTOMER if missing.
- Accepts with/without `ROLE_` prefix.
- Allows CUSTOMER and SELLER.
- Explicitly blocks ADMIN self-registration.

### Function: login(AuthRequest request)
Purpose:
- Authenticates credentials and returns JWT.

Security behavior:
- Special admin credential path using configured `app.admin.email` and `app.admin.password`.
- For normal users:
  - validates credentials with `AuthenticationManager`
  - throws `BadCredentialsException` on failure
  - loads DB user details and returns signed JWT.

### Function: getProfile(String email)
Purpose:
- Returns customer profile for authenticated user identity.

### Function: getSellerProfile(String email)
Purpose:
- Returns seller profile (including role).

---

## File: src/main/java/com/mobilezbd/controller/AuthController.java

Why security-related:
- Exposes auth endpoints.

### Function: register(RegisterRequest request)
- Endpoint: `POST /api/auth/register`
- Validates request and delegates to `UserService.register`.
- Returns `201 Created` with token + identity payload.

### Function: login(AuthRequest request)
- Endpoint: `POST /api/auth/login`
- Validates request and delegates to `UserService.login`.
- Returns `200 OK` with token + identity payload.

---

## File: src/main/java/com/mobilezbd/controller/AdminController.java

Security aspect:
- Class-level `@PreAuthorize("hasRole('ADMIN')")`.
- All methods require authenticated ADMIN role.

Methods protected by this class-level guard:
- `addProduct(...)`
- `updateProduct(...)`
- `deleteProduct(...)`
- `getProducts(...)`
- `getOrders(...)`
- `updateOrder(...)`
- `deleteOrder(...)`

---

## File: src/main/java/com/mobilezbd/controller/SellerController.java

Security aspect:
- Class-level `@PreAuthorize("hasRole('SELLER')")`.
- All methods require SELLER role.

Methods protected:
- `addProduct(...)`
- `getProducts(...)`
- `updateProduct(...)`
- `deleteProduct(...)`
- `profile(...)`
- `myOrders(...)`

---

## File: src/main/java/com/mobilezbd/controller/CustomerAccountController.java

Security aspect:
- Method-level `@PreAuthorize("hasRole('CUSTOMER')")` on profile endpoint.

Protected method:
- `profile(Principal principal)` -> only CUSTOMER can access.

---

## File: src/main/java/com/mobilezbd/controller/CustomerInfoController.java

Security aspects by method:
- `patchStock(...)`:
  - `@PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")`
- `createOrder(...)`:
  - `@PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")`
- `getMyOrders(...)`:
  - `@PreAuthorize("hasRole('CUSTOMER')")`

This gives fine-grained role restrictions per endpoint.

---

## File: src/main/java/com/mobilezbd/entity/User.java

Security relevance:
- Stores user identity and credentials.
- Fields:
  - `email` (unique username)
  - `password` (hashed)
  - `role` (`UserRole` enum)

## File: src/main/java/com/mobilezbd/entity/UserRole.java

Security relevance:
- Defines role constants used for authorization:
  - `ROLE_ADMIN`
  - `ROLE_CUSTOMER`
  - `ROLE_SELLER`

---

## File: src/main/resources/application.properties

Security-related properties:
- `app.jwt.secret` -> signing secret used by JWT.
- `app.jwt.expiration-ms` -> token lifetime.
- `app.admin.email` and `app.admin.password` -> configured admin login credentials.

---

## 3) Security-Related Frontend Functions

These do not enforce backend security alone, but implement client session handling and UI access control.

## File: frontend/src/api/axiosConfig.js

### Request interceptor
Purpose:
- Reads token from localStorage.
- Adds `Authorization: Bearer <token>` header for API requests.

### Response interceptor
Purpose:
- Retries retryable requests on network/502/503/504 errors.
- Includes login/register retry behavior for temporary backend unavailability.

---

## File: frontend/src/App.jsx

### Function: ProtectedRoute({ children, allowedRoles })
Purpose:
- Client-side route guard.

Behavior:
- If no token in localStorage -> redirect to `/login`.
- If role not in allowedRoles -> redirect to `/`.
- Otherwise render protected component.

Protected route usage:
- `/checkout` -> CUSTOMER or SELLER
- `/admin` -> ADMIN
- `/customer-account` -> CUSTOMER
- `/seller`, `/seller-account` -> SELLER

---

## File: frontend/src/pages/Login.jsx

### Function: handleSubmit(e)
Security role:
- Calls login API.
- Stores returned token/role/name in localStorage.
- Redirects user by role after successful authentication.

---

## File: frontend/src/pages/Register.jsx

### Function: handleSubmit(e)
Security role:
- Calls register API.
- Stores token/role/name from response.
- Starts authenticated session on successful registration.

---

## File: frontend/src/components/Navbar.jsx

### Function: handleLogout()
Security role:
- Removes token, role, and name from localStorage.
- Redirects to login page.

---

## 4) End-to-End Security Flow Summary

1. User registers or logs in through `/api/auth/*`.
2. Backend validates credentials, hashes/checks password, and generates JWT.
3. Frontend stores token and sends it in `Authorization` header for protected API calls.
4. `JwtAuthenticationFilter` validates token and sets authenticated principal.
5. `SecurityConfig` + `@PreAuthorize` enforce role access at URL and method levels.
6. Unauthorized requests are denied by Spring Security.

---

## 5) Direct Answer (Concise)

This project uses:
- Authentication: stateless JWT Bearer authentication.
- Authorization: role-based access control (RBAC) with `ROLE_ADMIN`, `ROLE_CUSTOMER`, and `ROLE_SELLER` enforced via Spring Security URL rules and `@PreAuthorize`.
- Password security: BCrypt hashing.
