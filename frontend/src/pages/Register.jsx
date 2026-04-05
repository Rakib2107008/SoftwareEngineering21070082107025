import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../api/authApi";
import { FaUserPlus } from "react-icons/fa";

function Register() {
  const [form, setForm] = useState({ name: "", email: "", password: "", role: "ROLE_CUSTOMER" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const data = await registerUser(form);
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("name", data.name);
      navigate("/");
    } catch (err) {
      const payload = err?.response?.data;
      const serverMessage = typeof payload === "string" ? payload : payload?.message || payload?.error;
      if (!err?.response) {
        setError("Cannot connect to backend API. Make sure Spring Boot is running on port 9091.");
      } else {
        const status = err.response.status;
        setError(`Registration failed (HTTP ${status}): ${serverMessage || "Unexpected server error"}`);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="page auth-page">
      <h2>Register</h2>
      <form className="form" onSubmit={handleSubmit}>
        <input placeholder="Name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input type="email" placeholder="Email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input type="password" placeholder="Password" required minLength={6} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
          <option value="ROLE_CUSTOMER">Customer</option>
          <option value="ROLE_SELLER">Seller</option>
        </select>
        {error && <p role="alert">{error}</p>}
        <button type="submit" className="btn-with-icon" disabled={submitting}>
          <FaUserPlus aria-hidden="true" />
          <span>{submitting ? "Registering..." : "Register"}</span>
        </button>
      </form>
      <Link to="/login">Already have account?</Link>
    </section>
  );
}

export default Register;
