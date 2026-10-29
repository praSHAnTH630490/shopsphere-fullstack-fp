import { useEffect } from "react";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { fetchWishlist, toggleWishlist } from "../redux/wishlistSlice";
import "../styles/pages/wishlist.css";

const FALLBACK_IMAGE = "https://placehold.co/600x450?text=Product";

export default function Wishlist() {
  const dispatch = useDispatch();
  const { data, loading } = useSelector((state) => state.wishlist);
  const items = data?.items || [];

  useEffect(() => {
    dispatch(fetchWishlist());
  }, [dispatch]);

  if (loading && items.length === 0) {
    return <main className="page section">Loading...</main>;
  }

  return (
    <main className="page section">
      <div className="eyebrow">SAVED FOR LATER</div>
      <h1>Wishlist</h1>

      {!items.length ? (
        <div className="empty-state">
          <h2>No saved products</h2>
          <p>Tap the heart on products you want to remember.</p>
          <Link className="primary-button" to="/products">
            Explore products
          </Link>
        </div>
      ) : (
        <div className="product-grid">
          {items.map((item) => (
            <article className="product-card" key={item.productId}>
              <div className="product-image">
                <img
                  src={item.imageUrl || FALLBACK_IMAGE}
                  alt={item.productName}
                />
              </div>

              <div className="product-body">
                <Link to={`/products/${item.productId}`}>
                  <h3>{item.productName}</h3>
                </Link>

                <strong>
                  ₹{Number(item.price || 0).toLocaleString("en-IN")}
                </strong>

                <button
                  type="button"
                  className="secondary-button full"
                  onClick={() =>
                    dispatch(
                      toggleWishlist({
                        productId: item.productId,
                        exists: true,
                      })
                    )
                  }
                >
                  Remove
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
    </main>
  );
}
