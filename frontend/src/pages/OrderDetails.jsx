import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { orderApi, getApiError } from "../services/api";
import Spinner from "../components/common/Spinner";
import "../styles/pages/order-details.css";

export default function OrderDetails() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    const load = async () => {
      try {
        const response = await orderApi.get(id);
        if (!cancelled) setOrder(response.data);
      } catch (e) {
        if (!cancelled) setError(getApiError(e));
      }
    };

    load();
    return () => { cancelled = true; };
  }, [id]);

  if (!order) {
    return (
      <main className="page">
        {error ? <div className="notice notice-error">{error}</div> : <Spinner />}
      </main>
    );
  }

  return (
    <main className="page section">
      <Link className="back-link" to="/orders">← All orders</Link>
      <div className="order-detail-head">
        <div>
          <div className="eyebrow">ORDER {order.orderNumber}</div>
          <h1>Order details</h1>
        </div>
        <span className={`status status-${String(order.status).toLowerCase()}`}>{order.status}</span>
      </div>
      <div className="panel order-items">
        {order.items?.map((item) => (
          <div className="order-item" key={item.id}>
            <div>
              <b>{item.productName}</b>
              <span>Qty {item.quantity} × ₹{Number(item.unitPrice || 0).toLocaleString("en-IN")}</span>
            </div>
            <strong>₹{Number(item.subtotal || 0).toLocaleString("en-IN")}</strong>
          </div>
        ))}
        <div className="order-total">
          <span>Total</span>
          <strong>₹{Number(order.totalAmount || 0).toLocaleString("en-IN")}</strong>
        </div>
      </div>
      <div className="order-meta">
        <div><span>Payment</span><b>{order.paymentMethod}</b></div>
        <div><span>Payment status</span><b>{order.paymentStatus}</b></div>
        <div><span>Placed</span><b>{new Date(order.createdAt).toLocaleString()}</b></div>
      </div>
    </main>
  );
}
