import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { orderApi, getApiError } from "../services/api";
import Spinner from "../components/common/Spinner";
import "../styles/pages/orders.css";

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = async () => {
    setLoading(true);
    try {
      const response = await orderApi.list();
      setOrders(response.data || []);
      setError("");
    } catch (e) {
      setError(getApiError(e));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let cancelled = false;
    orderApi.list()
      .then((response) => {
        if (!cancelled) {
          setOrders(response.data || []);
          setError("");
        }
      })
      .catch((e) => {
        if (!cancelled) {
          setError(getApiError(e));
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false);
        }
      });
    return () => { cancelled = true; };
  }, []);

  const cancel = async (id) => {
    if (!window.confirm("Cancel this order?")) return;
    try {
      await orderApi.cancel(id);
      await load();
    } catch (e) {
      setError(getApiError(e));
    }
  };

  return (
    <main className="page section">
      <div className="eyebrow">YOUR PURCHASES</div>
      <h1>Orders</h1>
      {error && <div className="notice notice-error">{error}</div>}
      {loading ? (
        <Spinner />
      ) : !orders.length ? (
        <div className="empty-state">
          <h2>No orders yet</h2>
          <Link className="primary-button" to="/products">Start shopping</Link>
        </div>
      ) : (
        <div className="order-list">
          {orders.map((order) => (
            <div className="order-card" key={order.id}>
              <div>
                <span className="muted">{order.orderNumber}</span>
                <h3>₹{Number(order.totalAmount || 0).toLocaleString("en-IN")}</h3>
                <p>{order.items?.length || 0} item(s) · {order.paymentMethod} · {order.paymentStatus}</p>
              </div>
              <div className="order-actions">
                <span className={`status status-${String(order.status).toLowerCase()}`}>{order.status}</span>
                <Link className="secondary-button" to={`/orders/${order.id}`}>View</Link>
                {!['CANCELLED', 'DELIVERED'].includes(order.status) && (
                  <button className="text-button" onClick={() => cancel(order.id)}>Cancel</button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </main>
  );
}
