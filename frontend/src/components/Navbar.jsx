import { Link, useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useState } from "react";
import { FaGift, FaSearch, FaShoppingCart, FaSignInAlt, FaSignOutAlt } from "react-icons/fa";
import { HiMenuAlt2 } from "react-icons/hi";

function Navbar({ onToggleSidebar }) {
  const { cart, cartCount, cartTotal, dispatch } = useCart();
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  const handleSearch = () => {
    navigate(`/cart?search=${encodeURIComponent(query)}`);
  };

  const token = localStorage.getItem("token");

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("name");
    navigate("/login");
  };

  return (
    <header className="navbar">
      <button className="menu-btn btn-with-icon" onClick={onToggleSidebar} type="button" aria-label="Open menu">
        <HiMenuAlt2 aria-hidden="true" />
      </button>
      <Link to="/" className="brand">MobileZBD</Link>
      <div className="search-wrap">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search products"
          className="search-input"
        />
        <button onClick={handleSearch} className="action-btn btn-with-icon" type="button">
          <FaSearch aria-hidden="true" />
          <span>Search</span>
        </button>
      </div>
      <div className="nav-actions">
        <button className="action-btn btn-with-icon" type="button">
          <FaGift aria-hidden="true" />
          <span>Offer</span>
        </button>
        <Link to="/cart-list" className="action-btn btn-with-icon">
          <FaShoppingCart aria-hidden="true" />
          <span>Cart ({cartCount})</span>
        </Link>
        {!token ? (
          <Link to="/login" className="action-btn btn-with-icon">
            <FaSignInAlt aria-hidden="true" />
            <span>Login</span>
          </Link>
        ) : (
          <button className="action-btn btn-with-icon" onClick={handleLogout} type="button">
            <FaSignOutAlt aria-hidden="true" />
            <span>Logout</span>
          </button>
        )}
      </div>
      <div className="cart-dropdown">
        <p>Quick Cart</p>
        {cart.length === 0 && <small>No items</small>}
        {cart.map((item) => (
          <div className="cart-row" key={item.productId}>
            <span>{item.name}</span>
            <span>${Number(item.price).toFixed(2)}</span>
            <button
              type="button"
              onClick={() => dispatch({ type: "REMOVE_FROM_CART", payload: { productId: item.productId } })}
            >
              x
            </button>
          </div>
        ))}
        <strong>Total: ${cartTotal.toFixed(2)}</strong>
      </div>
    </header>
  );
}

export default Navbar;
