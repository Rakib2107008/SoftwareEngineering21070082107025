import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../api/authApi";
import { FaSignInAlt } from "react-icons/fa";

function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const data = await loginUser(form);
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("name", data.name);
      if (data.role === "ROLE_ADMIN") {
        navigate("/admin");
      } else if (data.role === "ROLE_SELLER") {
        navigate("/seller");
      } else {
        navigate("/");
      }
    } catch (err) {
      const payload = err?.response?.data;
      const serverMessage = typeof payload === "string" ? payload : payload?.message || payload?.error;
      if (!err?.response) {
        setError("Cannot connect to backend API. Make sure Spring Boot is running on port 9091.");
      } else {
        const status = err.response.status;
        setError(`Login failed (HTTP ${status}): ${serverMessage || "Unexpected server error"}`);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="page auth-page">
      <h2>Login</h2>
      <form className="form" onSubmit={handleSubmit}>
        <input type="email" placeholder="Email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input type="password" placeholder="Password" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        {error && <p role="alert">{error}</p>}
        <button type="submit" className="btn-with-icon" disabled={submitting}>
          <FaSignInAlt aria-hidden="true" />
          <span>{submitting ? "Logging in..." : "Login"}</span>
        </button>
      </form>
      <Link to="/register">Create account</Link>
    </section>
  );
}

export default Login;
