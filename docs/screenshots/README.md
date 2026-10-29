# ShopSphere — Application Screenshots Guide

This directory holds visual documentation and UI captures from the running ShopSphere e-commerce application.

---

## 1. Primary Portfolio Screenshot

**Expected File Path:**  
`docs/screenshots/products-catalog.png`

### Description & Visible Features
- **Route:** `http://localhost:5173/products`
- **Catalog Navigation:** Grid of active products with responsive card layouts, thumbnail images, product titles, category eyebrows, brand labels, and localized INR (`₹`) prices.
- **Search & Multi-Facet Filters:** Real-time search bar, category dropdown filter, brand dropdown selector, price range bounds (Min ₹ / Max ₹), star rating filter, and sorting selector (Newest, Price Low to High, Price High to Low, Name A-Z, Oldest).
- **Wishlist Controls:** Quick-access interactive heart toggles (`♥` / `♡`) allowing one-click wishlist addition and removal for authenticated customers.
- **Stock Indicators:** "Add to Cart" action buttons that automatically reflect in-stock availability and dynamically disable when inventory reaches zero.

---

## 2. Additional Recommended UI Screenshots

To maximize impact when presenting this project to recruiters, capture the following pages from your local environment:

1. **`docs/screenshots/admin-dashboard.png`**:
   - Route: `/admin` (Logged in as `admin@store.com` / `Admin@123`)
   - Highlights: Metric summary cards (Total Revenue, Orders, Products, Customers), category/brand CRUD, and inventory stock manager.
2. **`docs/screenshots/shopping-cart.png`**:
   - Route: `/cart`
   - Highlights: Line-item product listings, quantity adjusters (`+` / `−`), dynamic subtotal recalculation, and order summary.
3. **`docs/screenshots/checkout-workflow.png`**:
   - Route: `/checkout`
   - Highlights: Saved address radio selection, inline address addition modal, and simulated payment options (Cash on Delivery, Simulated Card, Simulated UPI).
4. **`docs/screenshots/order-details.png`**:
   - Route: `/orders/:id`
   - Highlights: Order fulfillment status pill, ordered item summary, payment status, and order cancellation button.

---

## 3. How to Capture Screenshots from the Local App

1. Start the backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
2. Start the frontend:
   ```bash
   cd frontend
   npm run dev
   ```
3. Open `http://localhost:5173/products` in Chrome or Edge.
4. Press `F12` to open DevTools, toggle Device Toolbar or maximize to standard 1080p resolution (1920x1080).
5. Save the captured screenshot directly to `docs/screenshots/products-catalog.png`.
