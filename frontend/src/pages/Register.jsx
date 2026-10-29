import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { authApi, getApiError } from "../services/api";
import { useAuth } from "../context/AuthContext";
import "../styles/pages/auth.css";

const initialForm = {
  name: "",
  email: "",
  password: "",
  phone: "",
};

export default function Register() {
  const navigate = useNavigate();
  const { setSession } = useAuth();

  const [form, setForm] = useState(initialForm);
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
      const response = await authApi.register(form);
      setSession(response.data.token, response.data.user);
      navigate("/products", { replace: true });
    } catch (requestError) {
      setError(getApiError(requestError));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page">
      <div className="auth-card wide">
        <div className="auth-brand">● ShopSphere</div>
        <h1>Create your account</h1>
        <p>Join the store and start shopping.</p>

        {error && <div className="notice notice-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <label>
            Name
            <input name="name" required value={form.name} onChange={handleChange} />
          </label>

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
            Phone
            <input name="phone" value={form.phone} onChange={handleChange} />
          </label>

          <label>
            Password
            <input
              name="password"
              type="password"
              minLength={8}
              required
              value={form.password}
              onChange={handleChange}
            />
          </label>

          <button className="primary-button full" disabled={loading}>
            {loading ? "Creating..." : "Create account"}
          </button>
        </form>

        <p className="auth-switch">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </main>
  );
}
