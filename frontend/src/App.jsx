import { useState } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Cart from "./pages/Cart";
import CartDetails from "./pages/CartDetails";
import CartList from "./pages/CartList";
import CustomerForm from "./pages/CustomerForm";
import PaymentSlip from "./pages/PaymentSlip";
import Login from "./pages/Login";
import Register from "./pages/Register";
import AdminDashboard from "./pages/AdminDashboard";
import CustomerAccount from "./pages/CustomerAccount";
import "./App.css";

function ProtectedRoute({ children, allowedRoles }) {
  const role = localStorage.getItem("role");
  const token = localStorage.getItem("token");
  if (!token) return <Navigate to="/login" replace />;
  if (allowedRoles && !allowedRoles.includes(role)) return <Navigate to="/" replace />;
  return children;
}

function App() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  return (
    <div className="app-shell">
      <Navbar onToggleSidebar={() => setIsSidebarOpen((prev) => !prev)} />
      <Sidebar isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/product/:productId" element={<CartDetails />} />
          <Route path="/cart-list" element={<CartList />} />
          <Route
            path="/checkout"
            element={
              <ProtectedRoute allowedRoles={["ROLE_CUSTOMER"]}>
                <CustomerForm />
              </ProtectedRoute>
            }
          />
          <Route path="/payment-slip" element={<PaymentSlip />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/customer-account"
            element={
              <ProtectedRoute allowedRoles={["ROLE_CUSTOMER"]}>
                <CustomerAccount />
              </ProtectedRoute>
            }
          />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;
