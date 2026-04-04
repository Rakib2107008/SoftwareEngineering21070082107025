package com.mobilezbd.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    // Forward client-side routes to React entrypoint so refresh/deep links work.
    @GetMapping({
            "/",
            "/login",
            "/register",
            "/cart",
            "/cart-list",
            "/checkout",
            "/payment-slip",
            "/admin",
            "/customer-account",
            "/product/{productId}"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
