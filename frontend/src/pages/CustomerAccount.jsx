import { useEffect, useState } from "react";
import { getCustomerOrders, getCustomerProfile } from "../api/authApi";

function CustomerAccount() {
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
          getCustomerProfile(),
          getCustomerOrders(),
        ]);

        setProfile(profileRes || null);
        setOrders(Array.isArray(ordersRes) ? ordersRes : []);
      } catch {
        setProfile(null);
        setOrders([]);
        setError("Customer data could not be loaded. Please log in again.");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, []);

  return (
    <section className="page">
      <h2>Customer Account</h2>
      {loading && <p>Loading account...</p>}
      {!loading && error && <p>{error}</p>}
      {profile && (
        <div className="card-lite">
          <p>Name: {profile.name}</p>
          <p>Email: {profile.email}</p>
          <p>Note: {profile.account}</p>
        </div>
      )}
      <h3>Order History</h3>
      {!loading && !error && orders.length === 0 && <p>No orders yet.</p>}
      {orders.map((order) => (
        <div className="card-lite" key={order.id || `${order.productId}-${order.sellDate}`}>
          <p>{order.productName || "Product"}</p>
          <p>Qty: {order.quantity ?? order.soldQuantity ?? 0}</p>
          <p>Price: {order.unitPrice ?? order.sellPrice ?? 0}</p>
        </div>
      ))}
    </section>
  );
}

export default CustomerAccount;
