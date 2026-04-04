package com.mobilezbd.repository;

import com.mobilezbd.entity.ProductCategory;
import com.mobilezbd.entity.ProductDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long>, JpaSpecificationExecutor<ProductDetails> {
    List<ProductDetails> findByCategory(ProductCategory category);
}
