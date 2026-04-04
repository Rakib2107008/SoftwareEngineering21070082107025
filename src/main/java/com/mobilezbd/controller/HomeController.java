package com.mobilezbd.controller;

import com.mobilezbd.dto.ProductSummaryDto;
import com.mobilezbd.service.HomeService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<ProductSummaryDto>>> getHomeData() {
        return ResponseEntity.ok(homeService.getHomeData());
    }
}
