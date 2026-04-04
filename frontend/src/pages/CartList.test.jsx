import React from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it, vi } from "vitest";
import CartList from "./CartList";

vi.mock("../context/CartContext", () => ({
  useCart: () => ({
    cart: [
      { productId: 11, name: "Galaxy", quantity: 1, price: 99, image: "/api/products/11/image" },
    ],
    dispatch: vi.fn(),
    cartTotal: 99,
  }),
}));

vi.mock("../api/orderApi", () => ({
  validateCart: vi.fn(async () => ({ valid: true })),
}));

describe("CartList image behavior", () => {
  it("renders image from cart state dynamic path", () => {
    render(
      <MemoryRouter>
        <CartList />
      </MemoryRouter>,
    );

    const image = screen.getByAltText("Galaxy");
    expect(image.getAttribute("src")).toContain("/api/products/11/image");
  });
});
