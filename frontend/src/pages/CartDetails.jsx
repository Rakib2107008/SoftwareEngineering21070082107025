import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchProductDetails } from "../api/productApi";
import { useCart } from "../context/CartContext";
import { FaCartPlus } from "react-icons/fa";

function CartDetails() {
  const { productId } = useParams();
  const [product, setProduct] = useState(null);
  const { dispatch } = useCart();

  useEffect(() => {
    fetchProductDetails(productId).then(setProduct).catch(() => setProduct(null));
  }, [productId]);

  if (!product) return <p className="page">Loading...</p>;

  return (
    <section className="page details">
      <img src={product.image || "https://picsum.photos/600/450"} alt={product.name} />
      <div>
        <h2>{product.name}</h2>
        <h3>${Number(product.price).toFixed(2)}</h3>
        <p>Category: {product.category}</p>
        <p>Display: {product.display}</p>
        <p>Chipset: {product.chipset}</p>
        <p>Camera: {product.camera}</p>
        <p>Warranty: {product.warranty}</p>
        <p>Color: {product.color}</p>
        <p>Memory: {product.memory}</p>
        <p>UI: {product.ui}</p>
        <p>OS: {product.os}</p>
        <p>Battery: {product.battery}</p>
        <p>Release Date: {product.releaseDate}</p>
        <p>Stock: {product.quantity}</p>
        <button
          className="btn-with-icon"
          type="button"
          onClick={() =>
            dispatch({
              type: "ADD_TO_CART",
              payload: {
                productId: product.id,
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
      </div>
    </section>
  );
}

export default CartDetails;
