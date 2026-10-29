import { Link } from "react-router-dom";
import "../../styles/components/footer.css";

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-inner">
        <div>
          <Link className="footer-brand" to="/">
            ● ShopSphere
          </Link>
          <p>
            Simple shopping, reliable service and a clean full-stack experience.
          </p>
        </div>

        <div className="footer-links">
          <Link to="/products">Products</Link>
          <Link to="/about">About</Link>
          <Link to="/contact">Contact</Link>
          <Link to="/faq">FAQ</Link>
          <Link to="/profile">Profile</Link>
        </div>

        <p className="footer-copy">
          © {new Date().getFullYear()} ShopSphere · Built with React + Spring Boot
        </p>
      </div>
    </footer>
  );
}
