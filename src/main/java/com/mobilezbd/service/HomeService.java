package com.mobilezbd.service;

import com.mobilezbd.dto.ProductSummaryDto;
import com.mobilezbd.repository.ProductSellHistoryRepository;
import com.mobilezbd.repository.ProductsRepository;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final ProductsRepository productsRepository;
    private final ProductSellHistoryRepository sellHistoryRepository;

    public HomeService(ProductsRepository productsRepository, ProductSellHistoryRepository sellHistoryRepository) {
        this.productsRepository = productsRepository;
        this.sellHistoryRepository = sellHistoryRepository;
    }

    public Map<String, List<ProductSummaryDto>> getHomeData() {
        List<ProductSummaryDto> recent = productsRepository.findTop10ByOrderByIdDesc().stream()
                .map(ProductMapper::toSummary)
                .toList();

        List<Long> trendingDetailIds = sellHistoryRepository.findTrendingProductIds().stream()
                .limit(8)
                .toList();

        List<ProductSummaryDto> trending = productsRepository.findByProductDetailsIdIn(trendingDetailIds).stream()
                .sorted(Comparator.comparing(p -> trendingDetailIds.indexOf(p.getProductDetails().getId())))
                .limit(8)
                .map(ProductMapper::toSummary)
                .collect(Collectors.toList());

        Map<String, List<ProductSummaryDto>> response = new LinkedHashMap<>();
        response.put("recentlyAdded", recent);
        response.put("trendingProducts", trending);
        return response;
    }
}
