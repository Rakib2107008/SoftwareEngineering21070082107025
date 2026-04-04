import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { createOrder } from "../api/orderApi";
import { FaCheckCircle } from "react-icons/fa";

function CustomerForm() {
  const { cart, dispatch } = useCart();
  const [form, setForm] = useState({ name: "", address: "", email: "", mobileNumber: "", notes: "" });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const items = cart.map((item) => ({ productId: item.productId, quantity: item.quantity }));
    const summary = await createOrder({ customer: form, items });
    dispatch({ type: "CLEAR_CART" });
    navigate("/payment-slip", { state: summary });
  };

  return (
    <section className="page">
      <h2>Customer Information</h2>
      <form className="form" onSubmit={handleSubmit}>
        <input required placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input required placeholder="Address" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
        <input required type="email" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input required placeholder="Mobile Number" value={form.mobileNumber} onChange={(e) => setForm({ ...form, mobileNumber: e.target.value })} />
        <textarea placeholder="Notes" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
        <button type="submit" className="btn-with-icon">
          <FaCheckCircle aria-hidden="true" />
          <span>Confirm Order</span>
        </button>
      </form>
    </section>
  );
}

export default CustomerForm;
