import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import ProductCard from "./ProductCard";

let dispatchSpy;

vi.mock("../context/CartContext", () => ({
  useCart: () => ({ dispatch: dispatchSpy }),
}));

describe("ProductCard image behavior", () => {
  beforeEach(() => {
    dispatchSpy = vi.fn();
  });

  it("renders dynamic product image URL and stores stable image reference in cart", () => {
    const product = {
      id: 5,
      productDetailsId: 5,
      name: "Phone X",
      price: 100,
      imageUrl: "/api/products/5/image",
    };

    render(
      <MemoryRouter>
        <ProductCard product={product} />
      </MemoryRouter>,
    );

    const image = screen.getByAltText("Phone X");
    expect(image.getAttribute("src")).toContain("/api/products/5/image");

    fireEvent.click(screen.getByRole("button", { name: /add to cart/i }));

    expect(dispatchSpy).toHaveBeenCalledWith(
      expect.objectContaining({
        type: "ADD_TO_CART",
        payload: expect.objectContaining({
          productId: 5,
          image: "/api/products/5/image",
        }),
      }),
    );
  });
});
