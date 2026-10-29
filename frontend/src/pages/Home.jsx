import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { catalogApi, productApi, getApiError } from "../services/api";
import ProductCard from "../components/common/ProductCard";
import Spinner from "../components/common/Spinner";
import "../styles/pages/home.css";

export default function Home() {
  const [products, setProducts] = useState([]);
  const [cats, setCats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const [productsResponse, categoriesResponse] = await Promise.all([
          productApi.list({ page: 0, size: 8 }),
          catalogApi.categories()
        ]);
        if (!cancelled) {
          setProducts(productsResponse.data?.content || []);
          setCats(categoriesResponse.data || []);
        }
      } catch (e) {
        if (!cancelled) setError(getApiError(e));
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, []);

  return (
    <main>
      <section className="hero">
        <div>
          <div className="eyebrow">EVERYDAY SHOPPING, MADE EASY</div>
          <h1>Everything you need.<br /><span>One better place.</span></h1>
          <p>Discover quality products, simple checkout and reliable delivery — all in one clean shopping experience.</p>
          <div className="hero-actions">
            <Link className="primary-button large" to="/products">Shop products</Link>
            <Link className="secondary-button large" to="/register">Create account</Link>
          </div>
        </div>
        <div className="hero-art"><div className="hero-circle">🛍️</div><div className="float-card"><b>Fresh picks</b><span>New products every day</span></div></div>
      </section>
      <section className="page section">
        <div className="section-head"><div><div className="eyebrow">BROWSE</div><h2>Shop by category</h2></div><Link to="/products">View all →</Link></div>
        <div className="category-grid">{cats.slice(0, 6).map((category) => <Link className="category-card" key={category.id} to={`/products?category=${category.id}`}><div className="category-icon">{category.imageUrl ? <img src={category.imageUrl} alt="" /> : "✦"}</div><b>{category.name}</b><span>{category.description || "Explore products"}</span></Link>)}</div>
      </section>
      <section className="page section">
        <div className="section-head"><div><div className="eyebrow">POPULAR NOW</div><h2>Featured products</h2></div><Link to="/products">See everything →</Link></div>
        {loading ? <Spinner /> : error ? <div className="notice notice-error">{error}</div> : <div className="product-grid">{products.map((product) => <ProductCard key={product.id} product={product} />)}</div>}
      </section>
    </main>
  );
}
