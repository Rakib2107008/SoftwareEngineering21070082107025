import { describe, expect, it } from "vitest";
import { PRODUCT_IMAGE_PLACEHOLDER, resolveImageUrl, resolveProductImageUrl } from "./imageUrl";

describe("imageUrl utilities", () => {
  it("uses local placeholder fallback instead of external URL", () => {
    expect(PRODUCT_IMAGE_PLACEHOLDER).toBe("/assets/product-placeholder.svg");
    expect(PRODUCT_IMAGE_PLACEHOLDER.startsWith("http")).toBe(false);
    expect(resolveImageUrl("")).toBe("/assets/product-placeholder.svg");
  });

  it("builds dynamic endpoint path from product id when image ref is missing", () => {
    expect(resolveProductImageUrl("", 9)).toContain("/api/products/9/image");
  });
});
