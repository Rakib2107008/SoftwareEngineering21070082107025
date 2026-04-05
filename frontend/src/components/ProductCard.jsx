import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { FaCartPlus } from "react-icons/fa";

function ProductCard({ product }) {
  const { dispatch } = useCart();

  return (
    <article className="product-card">
      <Link to={`/product/${product.productDetailsId || product.id}`}>
        <img src={product.image || "https://picsum.photos/300/200"} alt={product.name} />
        <h4>{product.name}</h4>
      </Link>
      <p>${Number(product.price || 0).toFixed(2)}</p>
      {product.discount && <span className="badge">-{product.discount}%</span>}
      <button
        className="btn-with-icon"
        type="button"
        onClick={() =>
          dispatch({
            type: "ADD_TO_CART",
            payload: {
              productId: product.productDetailsId || product.id,
              name: product.name,
              image: product.image,
              price: product.price,
            },
          })
        }
      >
        <FaCartPlus aria-hidden="true" />
        <span>Add to Cart</span>
      </button>
    </article>
  );
}

export default ProductCard;
