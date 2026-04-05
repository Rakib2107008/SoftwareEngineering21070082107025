import { useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import { fetchCartProducts } from "../api/productApi";

function Cart() {
  const [searchParams] = useSearchParams();
  const [products, setProducts] = useState([]);
  const category = searchParams.get("category");
  const search = searchParams.get("search")?.toLowerCase() || "";

  useEffect(() => {
    fetchCartProducts(category).then(setProducts).catch(() => setProducts([]));
  }, [category]);

  const filtered = useMemo(
    () => products.filter((p) => p.name.toLowerCase().includes(search)),
    [products, search],
  );

  return (
    <section className="page">
      <h2>{category ? `${category.replace("_", " ")} Products` : "All Products"}</h2>
      <div className="product-grid">
        {filtered.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  );
}

export default Cart;
