import { Link, useNavigate } from "react-router-dom";
import { FaTimes, FaUserCircle } from "react-icons/fa";
import { categories, formatCategoryLabel, getCategoryIcon, HomeIcon } from "../utils/categoryMeta";

function Sidebar({ isOpen, onClose }) {
  const role = localStorage.getItem("role");
  const navigate = useNavigate();

  const goHome = () => {
    navigate("/");
    onClose();
  };

  const goCategory = (category) => {
    navigate(`/cart?category=${category}`);
    onClose();
  };

  return (
    <aside className={`sidebar ${isOpen ? "open" : ""}`}>
      <div className="sidebar-top">
        <h3>Categories</h3>
        <button
          type="button"
          className="sidebar-close-btn"
          onClick={onClose}
          aria-label="Close sidebar"
        >
          <FaTimes aria-hidden="true" />
        </button>
      </div>
      <div className="sidebar-home-section">
        <button type="button" className="sidebar-home-btn btn-with-icon" onClick={goHome}>
          <HomeIcon className="sidebar-icon" aria-hidden="true" />
          <span>Home</span>
        </button>
      </div>
      <div className="sidebar-grid">
        {categories.map((category) => {
          const Icon = getCategoryIcon(category);
          return (
            <button type="button" className="btn-with-icon" key={category} onClick={() => goCategory(category)}>
              <Icon className="sidebar-icon" aria-hidden="true" />
              <span>{formatCategoryLabel(category)}</span>
            </button>
          );
        })}
      </div>
      {role === "ROLE_CUSTOMER" && (
        <Link to="/customer-account" onClick={onClose} className="action-btn btn-with-icon">
          <FaUserCircle aria-hidden="true" />
          <span>Customer Account</span>
        </Link>
      )}
    </aside>
  );
}

export default Sidebar;
