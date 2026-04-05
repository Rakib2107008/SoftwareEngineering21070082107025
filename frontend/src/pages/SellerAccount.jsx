import { useEffect, useState } from "react";
import { getSellerOrders, getSellerProfile } from "../api/authApi";

function SellerAccount() {
  const [profile, setProfile] = useState(null);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      setError("");
      try {
        const [profileRes, ordersRes] = await Promise.all([
          getSellerProfile(),
          getSellerOrders(),
        ]);
        setProfile(profileRes || null);
        setOrders(Array.isArray(ordersRes) ? ordersRes : []);
      } catch {
        setProfile(null);
        setOrders([]);
        setError("Seller profile could not be loaded.");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  return (
    <section className="page">
      <h2>Seller Account</h2>
      {loading && <p>Loading seller profile...</p>}
      {!loading && error && <p>{error}</p>}
      {profile && (
        <div className="card-lite">
          <p>Name: {profile.name}</p>
          <p>Email: {profile.email}</p>
          <p>Role: {profile.role}</p>
          <p>Account Note: {profile.account}</p>
        </div>
      )}

      <h3>My Purchase History</h3>
      {!loading && !error && orders.length === 0 && <p>No purchases yet.</p>}
      {orders.map((order) => (
        <div className="card-lite" key={order.id || `${order.productId}-${order.sellDate}`}>
          <p>{order.productName || "Product"}</p>
          <p>Qty: {order.quantity ?? 0}</p>
          <p>Price: {Number(order.unitPrice || 0).toFixed(2)}</p>
          <p>Date: {order.sellDate ? new Date(order.sellDate).toLocaleString() : "N/A"}</p>
        </div>
      ))}
    </section>
  );
}

export default SellerAccount;
