import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../api/authApi";
import { FaSignInAlt } from "react-icons/fa";

function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await loginUser(form);
    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
    localStorage.setItem("name", data.name);
    navigate(data.role === "ROLE_ADMIN" ? "/admin" : "/");
  };

  return (
    <section className="page auth-page">
      <h2>Login</h2>
      <form className="form" onSubmit={handleSubmit}>
        <input type="email" placeholder="Email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input type="password" placeholder="Password" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <button type="submit" className="btn-with-icon">
          <FaSignInAlt aria-hidden="true" />
          <span>Login</span>
        </button>
      </form>
      <Link to="/register">Create account</Link>
    </section>
  );
}

export default Login;
