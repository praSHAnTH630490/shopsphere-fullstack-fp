import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";

import { useAuth } from "../../context/AuthContext";
import "../../styles/components/header.css";

export default function Header() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const cartCount = useSelector((state) =>
    (state.cart.data.items || []).reduce(
      (total, item) => total + Number(item.quantity || 0),
      0
    )
  );

  const isAdmin = (user?.roles || []).includes("ADMIN");

  const closeMenu = () => setMenuOpen(false);

  const handleLogout = () => {
    logout();
    closeMenu();
    navigate("/");
  };

  return (
    <header className="site-header">
      <div className="header-inner">
        <Link className="brand" to="/" onClick={closeMenu}>
          <span>●</span> ShopSphere
        </Link>

        <button
          type="button"
          className="menu-toggle"
          aria-label="Toggle navigation"
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((open) => !open)}
        >
          <span />
          <span />
          <span />
        </button>

        <nav className={`main-nav ${menuOpen ? "open" : ""}`}>
          <NavLink to="/" end onClick={closeMenu}>Home</NavLink>
          <NavLink to="/products" onClick={closeMenu}>Products</NavLink>

          {isAuthenticated && (
            <NavLink to="/wishlist" onClick={closeMenu}>Wishlist</NavLink>
          )}

          {isAuthenticated && (
            <NavLink to="/cart" onClick={closeMenu}>
              Cart <b className="nav-badge">{cartCount}</b>
            </NavLink>
          )}

          {isAuthenticated && (
            <NavLink to="/orders" onClick={closeMenu}>Orders</NavLink>
          )}

          {isAuthenticated && (
            <NavLink to="/profile" onClick={closeMenu}>Profile</NavLink>
          )}

          {isAdmin && (
            <NavLink to="/admin" onClick={closeMenu}>Admin</NavLink>
          )}
        </nav>

        <div className={`header-actions ${menuOpen ? "open" : ""}`}>
          {isAuthenticated ? (
            <>
              <span className="welcome-text">
                Hi, {user?.name || "Customer"}
              </span>
              <button type="button" className="outline-button" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link className="text-button" to="/login" onClick={closeMenu}>
                Login
              </Link>
              <Link className="primary-button" to="/register" onClick={closeMenu}>
                Sign up
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
