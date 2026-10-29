import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

import { authApi, getApiError } from "../services/api";
import { useAuth } from "../context/AuthContext";
import "../styles/pages/auth.css";

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const { setSession } = useAuth();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await authApi.login(form);
      const { token, user } = response.data;

      setSession(token, user);

      const requestedPath = location.state?.from?.pathname;
      const isAdmin = (user.roles || []).includes("ADMIN");

      navigate(requestedPath || (isAdmin ? "/admin" : "/products"), {
        replace: true,
      });
    } catch (requestError) {
      setError(getApiError(requestError));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page">
      <div className="auth-card">
        <div className="auth-brand">● ShopSphere</div>
        <h1>Welcome back</h1>
        <p>Sign in to continue shopping.</p>

        {error && <div className="notice notice-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <label>
            Email
            <input
              name="email"
              type="email"
              required
              value={form.email}
              onChange={handleChange}
            />
          </label>

          <label>
            Password
            <input
              name="password"
              type="password"
              required
              value={form.password}
              onChange={handleChange}
            />
          </label>

          <button className="primary-button full" disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
        </form>

        <p className="auth-switch">
          New here? <Link to="/register">Create an account</Link>
        </p>
      </div>
    </main>
  );
}
