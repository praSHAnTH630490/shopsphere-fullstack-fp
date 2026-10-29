import { Link } from "react-router-dom";
import "../styles/pages/not-found.css";

export default function NotFound() {
  return (
    <main className="page section">
      <div className="empty-state">
        <div className="eyebrow">404</div>
        <h1>Page not found</h1>
        <p>The page you requested does not exist.</p>
        <Link className="primary-button" to="/">
          Back to home
        </Link>
      </div>
    </main>
  );
}
