import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

import { addToCart } from "../../redux/cartSlice";
import { toggleWishlist } from "../../redux/wishlistSlice";
import { useAuth } from "../../context/AuthContext";
import "../../styles/components/product-card.css";

const FALLBACK_IMAGE = "https://placehold.co/600x450?text=Product";

export default function ProductCard({ product }) {
  const dispatch = useDispatch();
  const { isAuthenticated } = useAuth();

  const wishlistItems = useSelector(
    (state) => state.wishlist.data.items || []
  );

  const isWishlisted = wishlistItems.some(
    (item) => item.productId === product.id
  );

  const image = product.imageUrl || FALLBACK_IMAGE;
  const price = Number(product.price || 0).toLocaleString("en-IN");
  const isInStock = Number(product.stockQuantity || 0) > 0;

  const handleAddToCart = () => {
    if (!isAuthenticated || !isInStock) return;

    dispatch(
      addToCart({
        productId: product.id,
        quantity: 1,
      })
    );
  };

  const handleWishlist = () => {
    if (!isAuthenticated) return;

    dispatch(
      toggleWishlist({
        productId: product.id,
        exists: isWishlisted,
      })
    );
  };

  return (
    <article className="product-card">
      <div className="product-image">
        <img src={image} alt={product.name} />

        <button
          type="button"
          className="wish-btn"
          onClick={handleWishlist}
          aria-label={isWishlisted ? "Remove from wishlist" : "Add to wishlist"}
        >
          {isWishlisted ? "♥" : "♡"}
        </button>
      </div>

      <div className="product-body">
        <div className="eyebrow">{product.categoryName || "Shop"}</div>

        <Link to={`/products/${product.id}`}>
          <h3>{product.name}</h3>
        </Link>

        <p className="muted">{product.brandName || "Quality product"}</p>

        <div className="product-bottom">
          <strong>₹{price}</strong>

          <button
            type="button"
            className="small-btn"
            disabled={!isInStock}
            onClick={handleAddToCart}
          >
            {isInStock ? "Add" : "Out of stock"}
          </button>
        </div>
      </div>
    </article>
  );
}
