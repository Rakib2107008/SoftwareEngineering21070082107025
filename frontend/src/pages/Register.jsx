import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../api/authApi";
import { FaUserPlus } from "react-icons/fa";

function Register() {
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = await registerUser(form);
    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
    localStorage.setItem("name", data.name);
    navigate("/");
  };

  return (
    <section className="page auth-page">
      <h2>Register</h2>
      <form className="form" onSubmit={handleSubmit}>
        <input placeholder="Name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input type="email" placeholder="Email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input type="password" placeholder="Password" required minLength={6} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <button type="submit" className="btn-with-icon">
          <FaUserPlus aria-hidden="true" />
          <span>Register</span>
        </button>
      </form>
      <Link to="/login">Already have account?</Link>
    </section>
  );
}

export default Register;
