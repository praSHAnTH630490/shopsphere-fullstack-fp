import { useEffect, useState } from "react";

import Spinner from "../components/common/Spinner";
import { adminApi, getApiError } from "../services/api";
import "../styles/pages/admin.css";

const EMPTY_PRODUCT = {
  name: "",
  description: "",
  brandId: "",
  categoryId: "",
  price: "",
  stockQuantity: "",
  imageUrl: "",
  sku: "",
};

const EMPTY_GROUP = {
  name: "",
  description: "",
  imageUrl: "",
};

const ORDER_STATUSES = [
  "PLACED",
  "CONFIRMED",
  "PACKED",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
  "CANCELLED",
];

const TABS = [
  "overview",
  "products",
  "categories",
  "brands",
  "orders",
  "inventory",
];

export default function Admin() {
  const [dashboard, setDashboard] = useState(null);
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [orders, setOrders] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [tab, setTab] = useState("overview");

  const [product, setProduct] = useState(EMPTY_PRODUCT);
  const [editingProduct, setEditingProduct] = useState(null);
  const [category, setCategory] = useState(EMPTY_GROUP);
  const [editingCategory, setEditingCategory] = useState(null);
  const [brand, setBrand] = useState(EMPTY_GROUP);
  const [editingBrand, setEditingBrand] = useState(null);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const loadDashboard = async () => {
    setLoading(true);

    try {
      const [dashboardResponse, productsResponse, categoriesResponse, brandsResponse, ordersResponse, inventoryResponse] =
        await Promise.all([
          adminApi.dashboard(),
          adminApi.products(),
          adminApi.categories(),
          adminApi.brands(),
          adminApi.orders(),
          adminApi.inventory(),
        ]);

      setDashboard(dashboardResponse.data);
      setProducts(productsResponse.data || []);
      setCategories(categoriesResponse.data || []);
      setBrands(brandsResponse.data || []);
      setOrders(ordersResponse.data || []);
      setInventory(inventoryResponse.data || []);
      setError("");
    } catch (requestError) {
      setError(getApiError(requestError));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let cancelled = false;

    Promise.all([
      adminApi.dashboard(),
      adminApi.products(),
      adminApi.categories(),
      adminApi.brands(),
      adminApi.orders(),
      adminApi.inventory(),
    ])
      .then(
        ([
          dashboardResponse,
          productsResponse,
          categoriesResponse,
          brandsResponse,
          ordersResponse,
          inventoryResponse,
        ]) => {
          if (!cancelled) {
            setDashboard(dashboardResponse.data);
            setProducts(productsResponse.data || []);
            setCategories(categoriesResponse.data || []);
            setBrands(brandsResponse.data || []);
            setOrders(ordersResponse.data || []);
            setInventory(inventoryResponse.data || []);
            setError("");
          }
        }
      )
      .catch((requestError) => {
        if (!cancelled) {
          setError(getApiError(requestError));
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const showMessage = (text) => {
    setMessage(text);
    window.setTimeout(() => setMessage(""), 2500);
  };

  const saveProduct = async (event) => {
    event.preventDefault();
    setError("");

    try {
      const payload = {
        ...product,
        brandId: Number(product.brandId),
        categoryId: Number(product.categoryId),
        price: Number(product.price),
        stockQuantity: Number(product.stockQuantity),
      };

      if (editingProduct) {
        await adminApi.updateProduct(editingProduct, payload);
      } else {
        await adminApi.createProduct(payload);
      }

      resetProductForm();
      showMessage(editingProduct ? "Product updated." : "Product created.");
      await loadDashboard();
    } catch (requestError) {
      setError(getApiError(requestError));
    }
  };

  const saveGroup = async (type, event) => {
    event.preventDefault();
    setError("");

    const isCategory = type === "category";
    const form = isCategory ? category : brand;
    const editingId = isCategory ? editingCategory : editingBrand;

    try {
      if (isCategory) {
        if (editingId) {
          await adminApi.updateCategory(editingId, form);
        } else {
          await adminApi.createCategory(form);
        }
      } else if (editingId) {
        await adminApi.updateBrand(editingId, form);
      } else {
        await adminApi.createBrand(form);
      }

      if (isCategory) {
        setCategory(EMPTY_GROUP);
        setEditingCategory(null);
      } else {
        setBrand(EMPTY_GROUP);
        setEditingBrand(null);
      }

      showMessage(
        `${type[0].toUpperCase()}${type.slice(1)} ${editingId ? "updated" : "created"}.`
      );
      await loadDashboard();
    } catch (requestError) {
      setError(getApiError(requestError));
    }
  };

  const deactivate = async (type, id) => {
    if (!window.confirm(`Deactivate this ${type}?`)) return;

    setError("");

    try {
      if (type === "product") {
        await adminApi.removeProduct(id);
      } else if (type === "category") {
        await adminApi.removeCategory(id);
      } else {
        await adminApi.removeBrand(id);
      }

      showMessage(`${type[0].toUpperCase()}${type.slice(1)} deactivated.`);
      await loadDashboard();
    } catch (requestError) {
      setError(getApiError(requestError));
    }
  };

  const resetProductForm = () => {
    setProduct(EMPTY_PRODUCT);
    setEditingProduct(null);
  };

  const editProduct = (item) => {
    setProduct({
      name: item.name || "",
      description: item.description || "",
      brandId: item.brandId || "",
      categoryId: item.categoryId || "",
      price: item.price ?? "",
      stockQuantity: item.stockQuantity ?? "",
      imageUrl: item.imageUrl || "",
      sku: item.sku || "",
    });
    setEditingProduct(item.id);
  };

  if (loading && !dashboard) {
    return (
      <main className="page">
        <Spinner />
      </main>
    );
  }

  if (!dashboard) {
    return (
      <main className="page">
        <div className="notice notice-error">
          {error || "Unable to load admin dashboard."}
        </div>
      </main>
    );
  }

  return (
    <main className="page section">
      <header className="admin-head">
        <div>
          <div className="eyebrow">STORE CONTROL</div>
          <h1>Admin dashboard</h1>
          <p className="muted">
            Manage the catalog, orders and inventory using the real store APIs.
          </p>
        </div>
      </header>

      {error && <div className="notice notice-error">{error}</div>}
      {message && <div className="notice notice-success">{message}</div>}

      <div className="admin-tabs">
        {TABS.map((item) => (
          <button
            type="button"
            className={tab === item ? "active" : ""}
            key={item}
            onClick={() => setTab(item)}
          >
            {item}
          </button>
        ))}
      </div>

      {tab === "overview" && <DashboardStats dashboard={dashboard} />}

      {tab === "products" && (
        <div className="admin-grid">
          <ProductForm
            product={product}
            setProduct={setProduct}
            brands={brands}
            categories={categories}
            editing={editingProduct}
            onSubmit={saveProduct}
            onCancel={resetProductForm}
          />

          <div className="panel">
            <h2>Products</h2>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Name</th>
                    <th>SKU</th>
                    <th>Price</th>
                    <th>Stock</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map((item) => (
                    <tr key={item.id}>
                      <td>{item.name}</td>
                      <td>{item.sku}</td>
                      <td>₹{Number(item.price).toLocaleString("en-IN")}</td>
                      <td>{item.stockQuantity}</td>
                      <td>
                        <StatusBadge active={item.active} />
                      </td>
                      <td>
                        <button
                          type="button"
                          className="text-button"
                          onClick={() => editProduct(item)}
                        >
                          Edit
                        </button>
                        <button
                          type="button"
                          className="text-button danger"
                          onClick={() => deactivate("product", item.id)}
                          disabled={!item.active}
                        >
                          {item.active ? "Deactivate" : "Deactivated"}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {tab === "categories" && (
        <GroupManager
          title="Category"
          items={categories}
          form={category}
          setForm={setCategory}
          editing={editingCategory}
          setEditing={setEditingCategory}
          onSubmit={(event) => saveGroup("category", event)}
          onCancel={() => {
            setCategory(EMPTY_GROUP);
            setEditingCategory(null);
          }}
          onRemove={(id) => deactivate("category", id)}
        />
      )}

      {tab === "brands" && (
        <GroupManager
          title="Brand"
          items={brands}
          form={brand}
          setForm={setBrand}
          editing={editingBrand}
          setEditing={setEditingBrand}
          onSubmit={(event) => saveGroup("brand", event)}
          onCancel={() => {
            setBrand(EMPTY_GROUP);
            setEditingBrand(null);
          }}
          onRemove={(id) => deactivate("brand", id)}
        />
      )}

      {tab === "orders" && (
        <OrderManager
          orders={orders}
          onStatusChange={async (orderId, status) => {
            try {
              await adminApi.updateOrderStatus(orderId, status);
              showMessage("Order status updated.");
              await loadDashboard();
            } catch (requestError) {
              setError(getApiError(requestError));
            }
          }}
        />
      )}

      {tab === "inventory" && (
        <InventoryManager
          inventory={inventory}
          onSave={async (productId, stockQuantity) => {
            try {
              await adminApi.updateStock(productId, stockQuantity);
              showMessage("Inventory updated.");
              await loadDashboard();
            } catch (requestError) {
              setError(getApiError(requestError));
            }
          }}
        />
      )}
    </main>
  );
}

function DashboardStats({ dashboard }) {
  const stats = [
    ["Users", dashboard.totalUsers],
    ["Products", dashboard.totalProducts],
    ["Active products", dashboard.activeProducts],
    ["Categories", dashboard.totalCategories],
    ["Brands", dashboard.totalBrands],
    ["Orders", dashboard.totalOrders],
    [
      "Revenue",
      `₹${Number(dashboard.totalRevenue || 0).toLocaleString("en-IN")}`,
    ],
    ["Delivered", dashboard.deliveredOrders],
  ];

  return (
    <div className="stats-grid">
      {stats.map(([label, value]) => (
        <div className="stat-card" key={label}>
          <span>{label}</span>
          <strong>{value}</strong>
        </div>
      ))}
    </div>
  );
}

function StatusBadge({ active }) {
  return (
    <span className={active ? "status status--active" : "status status--inactive"}>
      {active ? "Active" : "Inactive"}
    </span>
  );
}

function ProductForm({
  product,
  setProduct,
  brands,
  categories,
  editing,
  onSubmit,
  onCancel,
}) {
  const updateField = (name, value) => {
    setProduct((current) => ({ ...current, [name]: value }));
  };

  return (
    <form className="panel" onSubmit={onSubmit}>
      <h2>{editing ? "Edit product" : "Add product"}</h2>

      <label>
        Name
        <input
          required
          value={product.name}
          onChange={(event) => updateField("name", event.target.value)}
        />
      </label>

      <label>
        Description
        <textarea
          value={product.description}
          onChange={(event) => updateField("description", event.target.value)}
        />
      </label>

      <label>
        Price
        <input
          required
          type="number"
          min="0"
          value={product.price}
          onChange={(event) => updateField("price", event.target.value)}
        />
      </label>

      <label>
        Stock
        <input
          required
          type="number"
          min="0"
          value={product.stockQuantity}
          onChange={(event) => updateField("stockQuantity", event.target.value)}
        />
      </label>

      <label>
        Image URL
        <input
          value={product.imageUrl}
          onChange={(event) => updateField("imageUrl", event.target.value)}
        />
      </label>

      <label>
        SKU
        <input
          required
          value={product.sku}
          onChange={(event) => updateField("sku", event.target.value)}
        />
      </label>

      <label>
        Brand
        <select
          required
          value={product.brandId}
          onChange={(event) => updateField("brandId", event.target.value)}
        >
          <option value="">Select</option>
          {brands.map((item) => (
            <option key={item.id} value={item.id}>
              {item.name}
            </option>
          ))}
        </select>
      </label>

      <label>
        Category
        <select
          required
          value={product.categoryId}
          onChange={(event) => updateField("categoryId", event.target.value)}
        >
          <option value="">Select</option>
          {categories.map((item) => (
            <option key={item.id} value={item.id}>
              {item.name}
            </option>
          ))}
        </select>
      </label>

      <div className="form-actions">
        <button className="primary-button">
          {editing ? "Update product" : "Create product"}
        </button>

        {editing && (
          <button type="button" className="secondary-button" onClick={onCancel}>
            Cancel
          </button>
        )}
      </div>
    </form>
  );
}

function GroupManager({
  title,
  items,
  form,
  setForm,
  editing,
  setEditing,
  onSubmit,
  onCancel,
  onRemove,
}) {
  const updateField = (name, value) => {
    setForm((current) => ({ ...current, [name]: value }));
  };

  return (
    <div className="admin-grid">
      <form className="panel" onSubmit={onSubmit}>
        <h2>{editing ? `Edit ${title}` : `Add ${title}`}</h2>

        <label>
          Name
          <input
            required
            value={form.name}
            onChange={(event) => updateField("name", event.target.value)}
          />
        </label>

        <label>
          Description
          <textarea
            value={form.description}
            onChange={(event) => updateField("description", event.target.value)}
          />
        </label>

        <label>
          Image URL
          <input
            value={form.imageUrl}
            onChange={(event) => updateField("imageUrl", event.target.value)}
          />
        </label>

        <div className="form-actions">
          <button className="primary-button">
            {editing ? "Update" : "Create"} {title}
          </button>

          {editing && (
            <button type="button" className="secondary-button" onClick={onCancel}>
              Cancel
            </button>
          )}
        </div>
      </form>

      <div className="panel">
        <h2>{title}s</h2>

        {items.map((item) => (
          <div className="saved-address" key={item.id}>
            <div>
              <b>{item.name}</b>
              <span>{item.description || "No description"}</span>
              <StatusBadge active={item.active} />
            </div>

            <div>
              <button
                type="button"
                className="text-button"
                onClick={() => {
                  setEditing(item.id);
                  setForm({
                    name: item.name || "",
                    description: item.description || "",
                    imageUrl: item.imageUrl || "",
                  });
                }}
              >
                Edit
              </button>

              <button
                type="button"
                className="text-button danger"
                onClick={() => onRemove(item.id)}
                disabled={!item.active}
              >
                {item.active ? "Deactivate" : "Deactivated"}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

function OrderManager({ orders, onStatusChange }) {
  return (
    <div className="panel">
      <h2>Orders</h2>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Order</th>
              <th>Total</th>
              <th>Payment</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td>{order.orderNumber}</td>
                <td>₹{Number(order.totalAmount).toLocaleString("en-IN")}</td>
                <td>{order.paymentStatus}</td>
                <td>
                  <select
                    value={order.status}
                    onChange={(event) =>
                      onStatusChange(order.id, event.target.value)
                    }
                  >
                    {ORDER_STATUSES.map((status) => (
                      <option key={status} value={status}>
                        {status}
                      </option>
                    ))}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function InventoryManager({ inventory, onSave }) {
  return (
    <div className="panel">
      <h2>Inventory</h2>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Product</th>
              <th>SKU</th>
              <th>Stock</th>
              <th>Save</th>
            </tr>
          </thead>
          <tbody>
            {inventory.map((item) => (
              <InventoryRow key={item.productId} item={item} onSave={onSave} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function InventoryRow({ item, onSave }) {
  const [stock, setStock] = useState(item.stockQuantity);

  return (
    <tr>
      <td>{item.productName}</td>
      <td>{item.sku}</td>
      <td>
        <input
          className="table-input"
          type="number"
          min="0"
          value={stock}
          onChange={(event) => setStock(event.target.value)}
        />
      </td>
      <td>
        <button
          type="button"
          className="text-button"
          onClick={() => onSave(item.productId, Number(stock))}
        >
          Save
        </button>
      </td>
    </tr>
  );
}
