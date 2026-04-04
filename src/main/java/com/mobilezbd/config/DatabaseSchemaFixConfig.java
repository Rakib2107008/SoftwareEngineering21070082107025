package com.mobilezbd.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;

@Configuration
public class DatabaseSchemaFixConfig {

    @Bean
    CommandLineRunner ensureImageColumnsAreText(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE product_details ALTER COLUMN image TYPE TEXT");
                jdbcTemplate.execute("ALTER TABLE products ALTER COLUMN image TYPE TEXT");
            } catch (DataAccessException ignored) {
                // Skip when tables are not ready (e.g., some test contexts).
            }
        };
    }
}