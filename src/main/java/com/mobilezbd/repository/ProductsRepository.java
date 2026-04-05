package com.mobilezbd.repository;

import com.mobilezbd.entity.Products;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductsRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {
    List<Products> findTop10ByOrderByIdDesc();
    List<Products> findByCategoryIgnoreCase(String category);
    List<Products> findByProductDetailsIdIn(List<Long> ids);
    List<Products> findByProductDetailsId(Long productDetailsId);
    List<Products> findByOwnerUserEmail(String ownerEmail);
    List<Products> findByOwnerUserEmailAndProductDetailsId(String ownerEmail, Long productDetailsId);
}
