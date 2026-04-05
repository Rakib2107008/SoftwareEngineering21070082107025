import { useEffect, useState } from "react";
import {
  createSellerProduct,
  deleteSellerProduct,
  fetchSellerProducts,
  updateSellerProduct,
} from "../api/productApi";

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

function SellerDashboard() {
  const [products, setProducts] = useState([]);
  const [form, setForm] = useState(blankProduct);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [imagePreview, setImagePreview] = useState("");

  const load = async () => {
    try {
      const rows = await fetchSellerProducts();
      setProducts(Array.isArray(rows) ? rows : []);
    } catch {
      setProducts([]);
      setError("Could not load seller products.");
    }
  };

  useEffect(() => {
    load();
  }, []);

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

  const handleImageFileChange = async (e) => {
    const file = e.target.files?.[0];
    if (!file) {
      return;
    }

    if (!file.type.startsWith("image/")) {
      setError("Please select a valid image file.");
      return;
    }

    if (file.size > 2 * 1024 * 1024) {
      setError("Image size must be 2MB or less.");
      return;
    }

    try {
      let dataUrl = await compressImageToDataUrl(file);
      if (!dataUrl || dataUrl.length > 950000) {
        dataUrl = await toDataUrl(file);
      }

      if (!dataUrl || dataUrl.length > 950000) {
        setError("Image is too large after processing. Please upload a smaller image.");
        return;
      }

      setForm((prev) => ({ ...prev, image: dataUrl }));
      setImagePreview(dataUrl);
      setError("");
    } catch (err) {
      setError(err?.message || "Image file could not be read.");
    }
  };

  const normalizePayload = (item) => ({
    ...item,
    price: Number(item.price),
    quantity: Number(item.quantity),
    releaseDate: item.releaseDate || null,
    display: item.display || null,
    chipset: item.chipset || null,
    camera: item.camera || null,
    warranty: item.warranty || null,
    color: item.color || null,
    memory: item.memory || null,
    ui: item.ui || null,
    os: item.os || null,
    battery: item.battery || null,
    image: item.image || null,
  });

  const save = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");
    try {
      const payload = normalizePayload(form);
      if (editingId) {
        await updateSellerProduct(editingId, payload);
        setMessage("Product updated.");
      } else {
        await createSellerProduct(payload);
        setMessage("Product created.");
      }
      setForm(blankProduct);
      setEditingId(null);
      setImagePreview("");
      load();
    } catch (err) {
      setError(err?.response?.data?.message || "Product save failed.");
    }
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setForm({
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

  return (
    <section className="page admin">
      <h2>Seller Dashboard</h2>
      {message && <p className="status-ok">{message}</p>}
      {error && <p className="status-error">{error}</p>}

      <form className="form admin-form-grid" onSubmit={save}>
        <input placeholder="Name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input placeholder="Category" required value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} />
        <input type="number" min="0" step="0.01" placeholder="Price" required value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} />
        <input type="date" value={form.releaseDate} onChange={(e) => setForm({ ...form, releaseDate: e.target.value })} />
        <input type="number" min="0" placeholder="Quantity" required value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} />
        <input placeholder="Display" value={form.display} onChange={(e) => setForm({ ...form, display: e.target.value })} />
        <input placeholder="Chipset" value={form.chipset} onChange={(e) => setForm({ ...form, chipset: e.target.value })} />
        <input placeholder="Camera" value={form.camera} onChange={(e) => setForm({ ...form, camera: e.target.value })} />
        <input placeholder="Warranty" value={form.warranty} onChange={(e) => setForm({ ...form, warranty: e.target.value })} />
        <input placeholder="Color" value={form.color} onChange={(e) => setForm({ ...form, color: e.target.value })} />
        <input placeholder="Memory" value={form.memory} onChange={(e) => setForm({ ...form, memory: e.target.value })} />
        <input placeholder="UI" value={form.ui} onChange={(e) => setForm({ ...form, ui: e.target.value })} />
        <input placeholder="OS" value={form.os} onChange={(e) => setForm({ ...form, os: e.target.value })} />
        <input placeholder="Battery" value={form.battery} onChange={(e) => setForm({ ...form, battery: e.target.value })} />
        <div className="admin-image-upload">
          <label htmlFor="sellerProductImage">Product Image File</label>
          <input id="sellerProductImage" type="file" accept="image/*" onChange={handleImageFileChange} />
          {(imagePreview || form.image) && (
            <img src={imagePreview || form.image} alt="Product preview" className="admin-image-preview" />
          )}
        </div>
        <button type="submit">{editingId ? "Update Product" : "Add Product"}</button>
      </form>

      <h3>My Products</h3>
      {products.length === 0 && <p>No products yet.</p>}
      {products.map((item) => (
        <div className="admin-row" key={item.id}>
          <span>{item.name} ({item.category}) - ${Number(item.price || 0).toFixed(2)}</span>
          <button type="button" onClick={() => startEdit(item)}>Edit</button>
          <button type="button" onClick={() => deleteSellerProduct(item.id).then(load)}>Delete</button>
        </div>
      ))}
    </section>
  );
}

export default SellerDashboard;
