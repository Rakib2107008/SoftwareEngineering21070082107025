import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { validateCart } from "../api/orderApi";
import { FaCheckCircle, FaMinus, FaPlus, FaTrash } from "react-icons/fa";

function CartList() {
  const { cart, dispatch, cartTotal } = useCart();
  const [sort, setSort] = useState("none");
  const [search, setSearch] = useState("");
  const [inStockOnly, setInStockOnly] = useState(false);
  const navigate = useNavigate();

  const displayed = useMemo(() => {
    let rows = [...cart];
    if (search) {
      rows = rows.filter((item) => item.name.toLowerCase().includes(search.toLowerCase()));
    }
    if (inStockOnly) {
      rows = rows.filter((item) => item.quantity > 0);
    }
    if (sort === "high") rows.sort((a, b) => b.price - a.price);
    if (sort === "low") rows.sort((a, b) => a.price - b.price);
    return rows;
  }, [cart, search, inStockOnly, sort]);

  const handleConfirm = async () => {
    const payload = cart.map((item) => ({ productId: item.productId, quantity: item.quantity }));
    const result = await validateCart(payload);
    if (result.valid) {
      navigate("/checkout");
      return;
    }
    alert(result.message);
  };

  return (
    <section className="page cart-list">
      <div>
        <h2>Cart List</h2>
        <div className="filters">
          <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search by name" />
          <select value={sort} onChange={(e) => setSort(e.target.value)}>
            <option value="none">Sort by</option>
            <option value="high">Price High</option>
            <option value="low">Price Low</option>
          </select>
          <label>
            <input type="checkbox" checked={inStockOnly} onChange={(e) => setInStockOnly(e.target.checked)} />
            In stock
          </label>
        </div>
        {displayed.map((item) => (
          <div className="cart-item" key={item.productId}>
            <img src={item.image || "https://picsum.photos/120/90"} alt={item.name} />
            <div>
              <strong>{item.name}</strong>
              <small>ID: {item.productId}</small>
            </div>
            <div className="qty-controls">
              <button
                className="btn-with-icon"
                type="button"
                onClick={() => dispatch({ type: "CHANGE_CART_QTY", payload: { productId: item.productId, quantity: item.quantity - 1 } })}
              >
                <FaMinus aria-hidden="true" />
              </button>
              <span>{item.quantity}</span>
              <button
                className="btn-with-icon"
                type="button"
                onClick={() => dispatch({ type: "CHANGE_CART_QTY", payload: { productId: item.productId, quantity: item.quantity + 1 } })}
              >
                <FaPlus aria-hidden="true" />
              </button>
            </div>
            <p>${Number(item.price).toFixed(2)}</p>
            <p>${(Number(item.price) * item.quantity).toFixed(2)}</p>
            <button
              className="btn-with-icon"
              type="button"
              onClick={() => dispatch({ type: "REMOVE_FROM_CART", payload: { productId: item.productId } })}
            >
              <FaTrash aria-hidden="true" />
              <span>Remove</span>
            </button>
          </div>
        ))}
      </div>
      <aside className="summary">
        <h3>Grand Total</h3>
        <p>${cartTotal.toFixed(2)}</p>
        <button type="button" className="btn-with-icon" onClick={handleConfirm}>
          <FaCheckCircle aria-hidden="true" />
          <span>Confirm</span>
        </button>
      </aside>
    </section>
  );
}

export default CartList;
