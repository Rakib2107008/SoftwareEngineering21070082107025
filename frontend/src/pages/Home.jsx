import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import { fetchHomeData, fetchProducts } from "../api/productApi";
import FilterBar from "../components/FilterBar";
import useDebouncedValue from "../hooks/useDebouncedValue";
import { categories, formatCategoryLabel, getCategoryIcon } from "../utils/categoryMeta";

const heroImages = [
  "/assets/hero-1.svg",
  "/assets/hero-2.svg",
  "/assets/hero-3.svg",
  "/assets/hero-4.svg",
];

function Home() {
  const [data, setData] = useState({ recentlyAdded: [], trendingProducts: [] });
  const [heroIndex, setHeroIndex] = useState(0);
  const [discoverProducts, setDiscoverProducts] = useState([]);
  const [discoverFilters, setDiscoverFilters] = useState({
    search: "",
    category: "",
    minPrice: "",
    maxPrice: "",
    sortBy: "newest",
  });

  const debouncedDiscoverSearch = useDebouncedValue(discoverFilters.search, 350);

  useEffect(() => {
    fetchHomeData().then(setData).catch(() => setData({ recentlyAdded: [], trendingProducts: [] }));
  }, []);

  useEffect(() => {
    fetchProducts({ ...discoverFilters, search: debouncedDiscoverSearch })
      .then((rows) => setDiscoverProducts(Array.isArray(rows) ? rows : []))
      .catch(() => setDiscoverProducts([]));
  }, [discoverFilters.category, discoverFilters.minPrice, discoverFilters.maxPrice, discoverFilters.sortBy, debouncedDiscoverSearch]);

  useEffect(() => {
    const timer = setInterval(() => {
      setHeroIndex((prev) => (prev + 1) % heroImages.length);
    }, 2600);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="page">
      <section className="hero-banner">
        <img src={heroImages[heroIndex]} alt="Phone shop" />
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

      <FilterBar
        title="Search Products"
        onClear={() => setDiscoverFilters({ search: "", category: "", minPrice: "", maxPrice: "", sortBy: "newest" })}
      >
        <input
          placeholder="Search by name"
          value={discoverFilters.search}
          onChange={(e) => setDiscoverFilters((prev) => ({ ...prev, search: e.target.value }))}
        />
        <select
          value={discoverFilters.category}
          onChange={(e) => setDiscoverFilters((prev) => ({ ...prev, category: e.target.value }))}
        >
          <option value="">All Categories</option>
          {categories.map((category) => (
            <option key={category} value={category}>{formatCategoryLabel(category)}</option>
          ))}
        </select>
        <input
          type="number"
          min="0"
          step="0.01"
          placeholder="Min Price"
          value={discoverFilters.minPrice}
          onChange={(e) => setDiscoverFilters((prev) => ({ ...prev, minPrice: e.target.value }))}
        />
        <input
          type="number"
          min="0"
          step="0.01"
          placeholder="Max Price"
          value={discoverFilters.maxPrice}
          onChange={(e) => setDiscoverFilters((prev) => ({ ...prev, maxPrice: e.target.value }))}
        />
        <select
          value={discoverFilters.sortBy}
          onChange={(e) => setDiscoverFilters((prev) => ({ ...prev, sortBy: e.target.value }))}
        >
          <option value="newest">Newest</option>
          <option value="nameAsc">Name A-Z</option>
          <option value="priceAsc">Price Low-High</option>
          <option value="priceDesc">Price High-Low</option>
        </select>
      </FilterBar>

      <section>
        <h2>Search Results</h2>
        {discoverProducts.length === 0 && <p className="empty-row">No products matched your search filters.</p>}
        <div className="product-grid">
          {discoverProducts.map((product) => <ProductCard key={product.id} product={product} />)}
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
