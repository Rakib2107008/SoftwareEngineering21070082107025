package com.mobilezbd.repository;

import com.mobilezbd.entity.ProductSellHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface ProductSellHistoryRepository extends JpaRepository<ProductSellHistory, Long>, JpaSpecificationExecutor<ProductSellHistory> {

    List<ProductSellHistory> findByCustomerCustomerId(Long customerId);

    @Query("""
            select psh.product.id
            from ProductSellHistory psh
            group by psh.product.id
            order by max(psh.sellDate) desc, sum(psh.soldQuantity) desc
            """)
    List<Long> findTrendingProductIds();

    @Query("""
            select psh
            from ProductSellHistory psh
            where psh.customer.user.email = :email
            order by psh.sellDate desc
            """)
    List<ProductSellHistory> findByUserEmail(@Param("email") String email);
}
