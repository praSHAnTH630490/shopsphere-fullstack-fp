import { useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import {
  clearCart,
  fetchCart,
  removeCartItem,
  updateCartItem,
} from "../redux/cartSlice";
import Spinner from "../components/common/Spinner";
import "../styles/pages/cart.css";

export default function Cart() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { data, loading, error } = useSelector((state) => state.cart);

  const items = data?.items || [];

  useEffect(() => {
    dispatch(fetchCart());
  }, [dispatch]);

  const formatPrice = (value) => Number(value || 0).toLocaleString("en-IN");

  const handleDecrease = (item) => {
    if (item.quantity > 1) {
      dispatch(
        updateCartItem({
          id: item.id,
          quantity: item.quantity - 1,
        })
      );
      return;
    }

    dispatch(removeCartItem(item.id));
  };

  const handleIncrease = (item) => {
    dispatch(
      updateCartItem({
        id: item.id,
        quantity: item.quantity + 1,
      })
    );
  };

  if (loading && items.length === 0) {
    return (
      <main className="page">
        <Spinner />
      </main>
    );
  }

  return (
    <main className="page section">
      <div className="section-head">
        <div>
          <div className="eyebrow">YOUR BAG</div>
          <h1>Shopping cart</h1>
        </div>

        {items.length > 0 && (
          <button
            type="button"
            className="text-button"
            onClick={() => dispatch(clearCart())}
          >
            Clear cart
          </button>
        )}
      </div>

      {error && <div className="notice notice-error">{error}</div>}

      {items.length === 0 ? (
        <div className="empty-state">
          <h2>Your cart is empty</h2>
          <p>Add something you like and it will appear here.</p>
          <Link className="primary-button" to="/products">
            Start shopping
          </Link>
        </div>
      ) : (
        <div className="cart-layout">
          <div className="cart-list">
            {items.map((item) => (
              <div className="cart-row" key={item.id}>
                <img
                  src={item.imageUrl || "https://placehold.co/160x130?text=Product"}
                  alt={item.productName}
                />

                <div className="cart-info">
                  <Link to={`/products/${item.productId}`}>
                    <h3>{item.productName}</h3>
                  </Link>

                  <span>₹{formatPrice(item.unitPrice)} each</span>

                  <div className="qty">
                    <button
                      type="button"
                      onClick={() => handleDecrease(item)}
                      aria-label="Decrease quantity"
                    >
                      −
                    </button>
                    <b>{item.quantity}</b>
                    <button
                      type="button"
                      onClick={() => handleIncrease(item)}
                      aria-label="Increase quantity"
                    >
                      +
                    </button>
                  </div>
                </div>

                <strong>₹{formatPrice(item.subtotal)}</strong>

                <button
                  type="button"
                  className="remove"
                  onClick={() => dispatch(removeCartItem(item.id))}
                >
                  Remove
                </button>
              </div>
            ))}
          </div>

          <aside className="summary">
            <h2>Order summary</h2>
            <div>
              <span>Total</span>
              <strong>₹{formatPrice(data.totalAmount)}</strong>
            </div>

            <button
              type="button"
              className="primary-button full"
              onClick={() => navigate("/checkout")}
            >
              Proceed to checkout
            </button>
          </aside>
        </div>
      )}
    </main>
  );
}
