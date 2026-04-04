package com.mobilezbd.repository;

import com.mobilezbd.entity.CustomerAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {
    Optional<CustomerAccount> findByUserEmail(String email);
}
