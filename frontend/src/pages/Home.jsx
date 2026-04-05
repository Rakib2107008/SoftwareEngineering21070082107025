import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import { fetchHomeData } from "../api/productApi";
import { categories, formatCategoryLabel, getCategoryIcon } from "../utils/categoryMeta";

function Home() {
  const [data, setData] = useState({ recentlyAdded: [], trendingProducts: [] });

  useEffect(() => {
    fetchHomeData().then(setData).catch(() => setData({ recentlyAdded: [], trendingProducts: [] }));
  }, []);

  return (
    <div className="page">
      <section>
        <div className="product-grid">
          <img src="https://images.unsplash.com/photo-1512428559087-560fa5ceab42?auto=format&fit=crop&w=1200&q=80" alt="Mobile phone shop interior" style={{ width: "100%", height: "220px", objectFit: "cover", borderRadius: "12px" }} />
          <img src="https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=1200&q=80" alt="Smartphones on display" style={{ width: "100%", height: "220px", objectFit: "cover", borderRadius: "12px" }} />
          <img src="https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=1200&q=80" alt="Mobile accessories shop" style={{ width: "100%", height: "220px", objectFit: "cover", borderRadius: "12px" }} />
        </div>
      </section>

      <section>
        <h2>Categories</h2>
        <div className="category-grid">
          {categories.map((c) => {
            const Icon = getCategoryIcon(c);
            return (
              <Link key={c} to={`/cart?category=${c}`} className="category-chip">
                <Icon className="category-icon" aria-hidden="true" />
                <span>{formatCategoryLabel(c)}</span>
              </Link>
            );
          })}
        </div>
      </section>

      <section>
        <h2>Recently Added</h2>
        <div className="product-grid">
          {data.recentlyAdded?.map((product) => <ProductCard key={product.id} product={product} />)}
        </div>
      </section>

      <section>
        <h2>Trending Products</h2>
        <div className="product-grid">
          {data.trendingProducts?.map((product) => <ProductCard key={product.id} product={product} />)}
        </div>
      </section>
    </div>
  );
}

export default Home;
