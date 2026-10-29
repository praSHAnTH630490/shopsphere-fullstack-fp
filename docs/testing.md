# ShopSphere — Testing & Quality Assurance Documentation

This document records the automated and manual testing strategy, test suites, regression test execution, and verification evidence for **ShopSphere**.

---

## 1. Automated Test Suites (Backend)

Automated tests are implemented using **JUnit 5 (Jupiter)** and **Mockito**, managed through Maven Surefire. Tests run statelessly without requiring a running MySQL database instance, ensuring high-speed continuous integration capability.

### 1.1 Summary of Automated Tests

| Test Suite | Target Class | Test Count | Status | Key Scenarios Covered |
| :--- | :--- | :---: | :---: | :--- |
| **`CartServiceTest`** | `CartService` | 6 | **PASS** | Adding new item, recalculating subtotals/totals, stock exceeding validation (HTTP 400), quantity updates, cart item removal, clearing cart. |
| **`OrderServiceTest`** | `OrderService` | 4 | **PASS** | Valid order placement, inventory stock deduction, empty cart rejection (HTTP 400), insufficient stock rejection (HTTP 400), order cancellation with inventory restock. |
| **`PaymentServiceTest`** | `PaymentService` | 4 | **PASS** | Successful simulation (state transition to `SUCCESS` + reference generation), failed simulation (`FAILED`), unauthorized user rejection, re-simulation on settled payments rejection. |
| **`AuthServiceTest`** | `AuthService` | 2 | **PASS** | New customer registration with password hashing and JWT issuance, duplicate email registration rejection (HTTP 400). |
| **`ProductServiceTest`** | `ProductService` | 3 | **PASS** | Specification-based multi-criteria search with pagination, negative page rejection, invalid page size boundary rejection. |

**Total Automated Tests:** 19  
**Failures:** 0  
**Errors:** 0  
**Execution Time:** ~2.5 seconds  

### 1.2 Automated Test Execution Command

Run the full test suite from the `backend/` directory:

```bash
mvn test
```

Expected output:

```text
[INFO] Results:
[INFO] 
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 2. Frontend Verification & Linting

### 2.1 Code Quality & Static Analysis
Frontend linting is enforced using **ESLint 10** with React 19 rules (`eslint-plugin-react-hooks` and `eslint-plugin-react-refresh`).

Command:

```bash
cd frontend
npm run lint
```

**Result:** PASS (0 errors, 0 warnings across all `.jsx` and `.js` source files).

### 2.2 Production Build Verification
The React Vite build bundles all JavaScript, CSS, and SVG assets into optimized production chunks.

Command:

```bash
cd frontend
npm run build
```

**Result:** PASS (`dist/` directory generated in < 1 second).

---

## 3. Manual QA Verification Matrix

The following test workflows have been validated against the application logic:

| Feature / Workflow | Tested Scenario | Expected Behavior | Verification Status |
| :--- | :--- | :--- | :---: |
| **User Registration** | Valid input: Name, Email, Password, Phone | Account created in DB with BCrypt password, customer role assigned, JWT returned. | **Verified** |
| **User Login** | Valid credentials | JWT token issued and stored in browser `localStorage`. Header updates with account avatar. | **Verified** |
| **Product Browsing** | Load `/products` page | Grid of active products loaded via `GET /api/products?page=0&size=12`. | **Verified** |
| **Search & Filtering** | Keyword search, Category dropdown, Price range | Query parameters updated in URL; backend `ProductSpecification` filters dynamically. | **Verified** |
| **Cart Quantity Addition** | Click `+` button in Cart | `PUT /api/cart/items/:id` increments quantity and updates total amount. | **Verified** |
| **Cart Stock Limit** | Quantity increment exceeding available stock | Backend rejects with HTTP 400 (`Requested quantity exceeds available stock`); Cart UI displays error notice. | **Verified** |
| **Wishlist Toggle** | Click heart icon on product card | If unauthenticated: redirects to login. If authenticated: adds/removes item via `POST/DELETE /api/wishlist/items/:id`. | **Verified** |
| **Delivery Address** | Save new address at Checkout | `POST /api/users/me/addresses` persists address; radio option auto-selected. | **Verified** |
| **Order Creation** | Checkout with items and delivery address | `POST /api/orders` creates order in `PLACED` status, decrements product stock, clears cart. | **Verified** |
| **Payment Simulation** | Card / UPI simulation at Checkout | `POST /api/payments/simulate?orderId=:id` sets status `SUCCESS` and records transaction reference `SIM-XXXX`. | **Verified** |
| **Order Cancellation** | Customer clicks "Cancel" on placed order | `POST /api/orders/:id/cancel` sets status `CANCELLED` and restores stock quantity to products. | **Verified** |
| **Admin Authorization** | Non-admin attempts to access `/admin` | `ProtectedRoute` blocks access and redirects unauthorized users. | **Verified** |
| **Admin Inventory Update** | Admin updates stock quantity in dashboard | `PATCH /api/admin/inventory/products/:id` updates product stock level. | **Verified** |

---

## 4. Regression & Boundary Test Cases

### 4.1 Boundary: Order Placement with Empty Cart
- **Precondition**: User is logged in with an empty cart.
- **Action**: Direct POST to `/api/orders` with a valid address.
- **Expected Result**: Backend throws `IllegalArgumentException: Cannot place an order with an empty cart`, returning HTTP 400 Bad Request.

### 4.2 Boundary: Out-of-Stock Product Purchase
- **Precondition**: Product has `stockQuantity = 2`. User has 2 in cart.
- **Action**: Another session purchases 1 unit, reducing stock to 1. User attempts to checkout with 2.
- **Expected Result**: Order service validates `product.getStockQuantity() < cartItem.getQuantity()` and aborts transaction with HTTP 400 (`Insufficient stock for product: [Name]`).

### 4.3 Security: IDOR on Order Lookup
- **Precondition**: Order #100 belongs to User A.
- **Action**: User B attempts `GET /api/orders/100`.
- **Expected Result**: Backend verifies user ownership and returns HTTP 400 / 403 (`You are not allowed to access this order`).
