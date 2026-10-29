import { useEffect } from "react";
import { useDispatch } from "react-redux";

import { fetchCart } from "./redux/cartSlice";
import { fetchWishlist } from "./redux/wishlistSlice";
import { useAuth } from "./context/AuthContext";
import { AppRoutes } from "./routes/AppRoutes";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";

function SessionDataLoader() {
  const dispatch = useDispatch();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) return;

    dispatch(fetchCart());
    dispatch(fetchWishlist());
  }, [dispatch, isAuthenticated]);

  return null;
}

export default function App() {
  return (
    <div className="app-shell">
      <SessionDataLoader />
      <Header />
      <div className="app-content">
        <AppRoutes />
      </div>
      <Footer />
    </div>
  );
}
