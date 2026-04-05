import { useEffect, useMemo, useState } from "react";
import {
  createAdminProduct,
  deleteAdminProduct,
  fetchAdminProducts,
  updateAdminProduct,
} from "../api/productApi";
import { deleteAdminOrder, fetchAdminOrders, updateAdminOrder } from "../api/orderApi";
import { FaEdit, FaPlusCircle, FaSave, FaTimesCircle, FaTrash } from "react-icons/fa";
import FilterBar from "../components/FilterBar";
import useDebouncedValue from "../hooks/useDebouncedValue";
import { categories } from "../utils/categoryMeta";

const blankProduct = {
  name: "",
  category: "SMARTPHONE",
  price: "",
  releaseDate: "",
  quantity: "",
  display: "",
  chipset: "",
  camera: "",
  warranty: "",
  color: "",
  memory: "",
  ui: "",
  os: "",
  battery: "",
  image: "",
};

const blankProductFilters = {
  search: "",
  category: "",
  releaseDateFrom: "",
  releaseDateTo: "",
  minPrice: "",
  maxPrice: "",
  minQty: "",
  maxQty: "",
};

const blankOrderFilters = {
  search: "",
  sellDateFrom: "",
  sellDateTo: "",
  minQty: "",
  maxQty: "",
  minPrice: "",
  maxPrice: "",
};

function AdminDashboard() {
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [productForm, setProductForm] = useState(blankProduct);
  const [editingProductId, setEditingProductId] = useState(null);
  const [productFilters, setProductFilters] = useState(blankProductFilters);
  const [orderFilters, setOrderFilters] = useState(blankOrderFilters);
  const [orderQtyDrafts, setOrderQtyDrafts] = useState({});
  const [statusMessage, setStatusMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [imagePreview, setImagePreview] = useState("");

  const debouncedProductSearch = useDebouncedValue(productFilters.search, 350);
  const debouncedOrderSearch = useDebouncedValue(orderFilters.search, 350);

  const productQuery = useMemo(
    () => ({ ...productFilters, search: debouncedProductSearch }),
    [productFilters, debouncedProductSearch],
  );
  const orderQuery = useMemo(
    () => ({ ...orderFilters, search: debouncedOrderSearch }),
    [orderFilters, debouncedOrderSearch],
  );

  const load = async () => {
    const [productResult, orderResult] = await Promise.allSettled([
      fetchAdminProducts(productQuery),
      fetchAdminOrders(orderQuery),
    ]);

    const nextErrors = [];

    if (productResult.status === "fulfilled") {
      const productRows = productResult.value;
      setProducts(Array.isArray(productRows) ? productRows : []);
    } else {
      setProducts([]);
      nextErrors.push("Products could not be loaded");
    }

    if (orderResult.status === "fulfilled") {
      const orderRows = orderResult.value;
      setOrders(Array.isArray(orderRows) ? orderRows : []);
      setOrderQtyDrafts((prev) => {
        const next = { ...prev };
        (orderRows || []).forEach((row) => {
          if (next[row.id] === undefined) {
            next[row.id] = row.quantity;
          }
        });
        return next;
      });
    } else {
      setOrders([]);
      nextErrors.push("Orders could not be loaded");
    }

    setErrorMessage(nextErrors.join(". "));
  };

  useEffect(() => {
    load();
  }, [productQuery, orderQuery]);

  const normalizeFormPayload = (formData) => ({
    ...formData,
    price: Number(formData.price),
    quantity: Number(formData.quantity),
    releaseDate: formData.releaseDate || null,
    display: formData.display || null,
    chipset: formData.chipset || null,
    camera: formData.camera || null,
    warranty: formData.warranty || null,
    color: formData.color || null,
    memory: formData.memory || null,
    ui: formData.ui || null,
    os: formData.os || null,
    battery: formData.battery || null,
    image: formData.image || null,
  });

  const toDataUrl = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(typeof reader.result === "string" ? reader.result : "");
      reader.onerror = () => reject(new Error("Image file could not be read."));
      reader.readAsDataURL(file);
    });

  const compressImageToDataUrl = (file, maxWidth = 900, quality = 0.8) =>
    new Promise((resolve, reject) => {
      const srcUrl = URL.createObjectURL(file);
      const image = new Image();
      image.onload = () => {
        const ratio = image.width > maxWidth ? maxWidth / image.width : 1;
        const canvas = document.createElement("canvas");
        canvas.width = Math.max(1, Math.round(image.width * ratio));
        canvas.height = Math.max(1, Math.round(image.height * ratio));
        const context = canvas.getContext("2d");
        if (!context) {
          URL.revokeObjectURL(srcUrl);
          reject(new Error("Image processing is not supported in this browser."));
          return;
        }
        context.drawImage(image, 0, 0, canvas.width, canvas.height);
        const dataUrl = canvas.toDataURL("image/jpeg", quality);
        URL.revokeObjectURL(srcUrl);
        resolve(dataUrl);
      };
      image.onerror = () => {
        URL.revokeObjectURL(srcUrl);
        reject(new Error("Image processing failed."));
      };
      image.src = srcUrl;
    });

  const saveProduct = async (e) => {
    e.preventDefault();
    setStatusMessage("");
    setErrorMessage("");
    try {
      const payload = normalizeFormPayload(productForm);
      if (editingProductId) {
        await updateAdminProduct(editingProductId, payload);
        setStatusMessage("Product updated successfully.");
      } else {
        await createAdminProduct(payload);
        setStatusMessage("Product added successfully.");
      }
      setProductForm(blankProduct);
      setEditingProductId(null);
      setImagePreview("");
      load();
    } catch (error) {
      const backendMessage = error?.response?.data?.message;
      setErrorMessage(backendMessage || "Product save failed. Please check input values.");
    }
  };

  const startEdit = (item) => {
    setEditingProductId(item.id);
    setProductForm({
      name: item.name || "",
      category: item.category || "SMARTPHONE",
      price: item.price ?? "",
      releaseDate: item.releaseDate || "",
      quantity: item.quantity ?? "",
      display: item.display || "",
      chipset: item.chipset || "",
      camera: item.camera || "",
      warranty: item.warranty || "",
      color: item.color || "",
      memory: item.memory || "",
      ui: item.ui || "",
      os: item.os || "",
      battery: item.battery || "",
      image: item.image || "",
    });
    setImagePreview(item.image || "");
  };

  const cancelEdit = () => {
    setEditingProductId(null);
    setProductForm(blankProduct);
    setImagePreview("");
  };

  const handleImageFileChange = async (e) => {
    const file = e.target.files?.[0];
    if (!file) {
      return;
    }

    if (!file.type.startsWith("image/")) {
      setErrorMessage("Please select a valid image file.");
      return;
    }

    if (file.size > 2 * 1024 * 1024) {
      setErrorMessage("Image size must be 2MB or less.");
      return;
    }

    try {
      let dataUrl = await compressImageToDataUrl(file);
      if (!dataUrl || dataUrl.length > 950000) {
        dataUrl = await toDataUrl(file);
      }

      if (!dataUrl || dataUrl.length > 950000) {
        setErrorMessage("Image is too large after processing. Please upload a smaller image.");
        return;
      }

      setProductForm((prev) => ({ ...prev, image: dataUrl }));
      setImagePreview(dataUrl);
      setErrorMessage("");
    } catch (error) {
      setErrorMessage(error?.message || "Image file could not be read.");
    }
  };

  const updateOrderQty = async (orderId) => {
    const qty = Number(orderQtyDrafts[orderId]);
    if (!qty || qty < 1) {
      setErrorMessage("Order quantity must be at least 1.");
      return;
    }
    try {
      await updateAdminOrder(orderId, qty);
      setStatusMessage("Order quantity updated.");
      setErrorMessage("");
      load();
    } catch {
      setErrorMessage("Order update failed.");
    }
  };

  const clearProductFilters = () => setProductFilters(blankProductFilters);
  const clearOrderFilters = () => setOrderFilters(blankOrderFilters);

  return (
    <section className="page admin">
      <h2>Admin Dashboard</h2>
      {statusMessage && <p className="status-ok">{statusMessage}</p>}
      {errorMessage && <p className="status-error">{errorMessage}</p>}

      <form className="form admin-form-grid" onSubmit={saveProduct}>
        <input placeholder="Name" required value={productForm.name} onChange={(e) => setProductForm({ ...productForm, name: e.target.value })} />
        <select value={productForm.category} onChange={(e) => setProductForm({ ...productForm, category: e.target.value })}>
          {categories.map((category) => (
            <option key={category} value={category}>{category.replace(/_/g, " ")}</option>
          ))}
        </select>
        <input type="number" min="0" step="0.01" placeholder="Price" required value={productForm.price} onChange={(e) => setProductForm({ ...productForm, price: e.target.value })} />
        <input type="date" placeholder="Release Date" value={productForm.releaseDate} onChange={(e) => setProductForm({ ...productForm, releaseDate: e.target.value })} />
        <input type="number" min="0" placeholder="Quantity" required value={productForm.quantity} onChange={(e) => setProductForm({ ...productForm, quantity: e.target.value })} />
        <input placeholder="Display" value={productForm.display} onChange={(e) => setProductForm({ ...productForm, display: e.target.value })} />
        <input placeholder="Chipset" value={productForm.chipset} onChange={(e) => setProductForm({ ...productForm, chipset: e.target.value })} />
        <input placeholder="Camera" value={productForm.camera} onChange={(e) => setProductForm({ ...productForm, camera: e.target.value })} />
        <input placeholder="Warranty" value={productForm.warranty} onChange={(e) => setProductForm({ ...productForm, warranty: e.target.value })} />
        <input placeholder="Color" value={productForm.color} onChange={(e) => setProductForm({ ...productForm, color: e.target.value })} />
        <input placeholder="Memory" value={productForm.memory} onChange={(e) => setProductForm({ ...productForm, memory: e.target.value })} />
        <input placeholder="UI" value={productForm.ui} onChange={(e) => setProductForm({ ...productForm, ui: e.target.value })} />
        <input placeholder="OS" value={productForm.os} onChange={(e) => setProductForm({ ...productForm, os: e.target.value })} />
        <input placeholder="Battery" value={productForm.battery} onChange={(e) => setProductForm({ ...productForm, battery: e.target.value })} />
        <div className="admin-image-upload">
          <label htmlFor="productImage">Product Image File</label>
          <input id="productImage" type="file" accept="image/*" onChange={handleImageFileChange} />
          {(imagePreview || productForm.image) && (
            <img src={imagePreview || productForm.image} alt="Product preview" className="admin-image-preview" />
          )}
        </div>

        <div className="admin-form-actions">
          <button type="submit" className="btn-with-icon">
            {editingProductId ? <FaSave aria-hidden="true" /> : <FaPlusCircle aria-hidden="true" />}
            <span>{editingProductId ? "Update Product" : "Add Product"}</span>
          </button>
          {editingProductId && (
            <button type="button" className="btn-with-icon" onClick={cancelEdit}>
              <FaTimesCircle aria-hidden="true" />
              <span>Cancel Edit</span>
            </button>
          )}
        </div>
      </form>

      <h3>Products</h3>
      <FilterBar title="Product Filters" onClear={clearProductFilters}>
        <input placeholder="Search by name" value={productFilters.search} onChange={(e) => setProductFilters((prev) => ({ ...prev, search: e.target.value }))} />
        <select value={productFilters.category} onChange={(e) => setProductFilters((prev) => ({ ...prev, category: e.target.value }))}>
          <option value="">All Categories</option>
          {categories.map((category) => (
            <option key={category} value={category}>{category.replace(/_/g, " ")}</option>
          ))}
        </select>
        <input type="date" value={productFilters.releaseDateFrom} onChange={(e) => setProductFilters((prev) => ({ ...prev, releaseDateFrom: e.target.value }))} />
        <input type="date" value={productFilters.releaseDateTo} onChange={(e) => setProductFilters((prev) => ({ ...prev, releaseDateTo: e.target.value }))} />
        <input type="number" min="0" step="0.01" placeholder="Min Price" value={productFilters.minPrice} onChange={(e) => setProductFilters((prev) => ({ ...prev, minPrice: e.target.value }))} />
        <input type="number" min="0" step="0.01" placeholder="Max Price" value={productFilters.maxPrice} onChange={(e) => setProductFilters((prev) => ({ ...prev, maxPrice: e.target.value }))} />
        <input type="number" min="0" placeholder="Min Qty" value={productFilters.minQty} onChange={(e) => setProductFilters((prev) => ({ ...prev, minQty: e.target.value }))} />
        <input type="number" min="0" placeholder="Max Qty" value={productFilters.maxQty} onChange={(e) => setProductFilters((prev) => ({ ...prev, maxQty: e.target.value }))} />
      </FilterBar>

      {products.length === 0 && <p className="empty-row">No products found for selected filters.</p>}
      {products.length > 0 && (
        <div className="table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Qty</th>
                <th>Release Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.map((item) => (
                <tr key={item.id}>
                  <td>{item.name}</td>
                  <td>{item.category?.replace(/_/g, " ")}</td>
                  <td>${Number(item.price || 0).toFixed(2)}</td>
                  <td>{item.quantity}</td>
                  <td>{item.releaseDate || "N/A"}</td>
                  <td className="table-actions">
                    <button className="btn-with-icon" type="button" onClick={() => startEdit(item)}>
                      <FaEdit aria-hidden="true" />
                      <span>Update</span>
                    </button>
                    <button className="btn-with-icon" type="button" onClick={() => deleteAdminProduct(item.id).then(load)}>
                      <FaTrash aria-hidden="true" />
                      <span>Delete</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <h3>Orders</h3>
      <FilterBar title="Order Filters" onClear={clearOrderFilters}>
        <input placeholder="Search by product" value={orderFilters.search} onChange={(e) => setOrderFilters((prev) => ({ ...prev, search: e.target.value }))} />
        <input type="date" value={orderFilters.sellDateFrom} onChange={(e) => setOrderFilters((prev) => ({ ...prev, sellDateFrom: e.target.value }))} />
        <input type="date" value={orderFilters.sellDateTo} onChange={(e) => setOrderFilters((prev) => ({ ...prev, sellDateTo: e.target.value }))} />
        <input type="number" min="0" placeholder="Min Qty" value={orderFilters.minQty} onChange={(e) => setOrderFilters((prev) => ({ ...prev, minQty: e.target.value }))} />
        <input type="number" min="0" placeholder="Max Qty" value={orderFilters.maxQty} onChange={(e) => setOrderFilters((prev) => ({ ...prev, maxQty: e.target.value }))} />
        <input type="number" min="0" step="0.01" placeholder="Min Unit Price" value={orderFilters.minPrice} onChange={(e) => setOrderFilters((prev) => ({ ...prev, minPrice: e.target.value }))} />
        <input type="number" min="0" step="0.01" placeholder="Max Unit Price" value={orderFilters.maxPrice} onChange={(e) => setOrderFilters((prev) => ({ ...prev, maxPrice: e.target.value }))} />
      </FilterBar>

      {orders.length === 0 && <p className="empty-row">No orders found for selected filters.</p>}
      {orders.length > 0 && (
        <div className="table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Customer</th>
                <th>Product</th>
                <th>Qty</th>
                <th>Unit Price</th>
                <th>Total</th>
                <th>Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id || `${order.productId}-${order.sellDate}`}>
                  <td>{order.customerName || "N/A"}</td>
                  <td>{order.productName}</td>
                  <td>
                    <input
                      type="number"
                      min="1"
                      className="order-qty-input"
                      value={orderQtyDrafts[order.id] ?? order.quantity}
                      onChange={(e) => setOrderQtyDrafts((prev) => ({ ...prev, [order.id]: e.target.value }))}
                    />
                  </td>
                  <td>${Number(order.unitPrice || 0).toFixed(2)}</td>
                  <td>${Number(order.lineTotal || 0).toFixed(2)}</td>
                  <td>{order.sellDate ? new Date(order.sellDate).toLocaleDateString() : "N/A"}</td>
                  <td className="table-actions">
                    <button className="btn-with-icon" type="button" onClick={() => updateOrderQty(order.id)}>
                      <FaSave aria-hidden="true" />
                      <span>Update</span>
                    </button>
                    <button className="btn-with-icon" type="button" onClick={() => deleteAdminOrder(order.id).then(load)}>
                      <FaTrash aria-hidden="true" />
                      <span>Delete</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

export default AdminDashboard;
