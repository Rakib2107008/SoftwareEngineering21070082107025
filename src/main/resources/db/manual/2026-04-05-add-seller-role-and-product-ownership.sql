-- Manual migration for seller role + product ownership
-- Run this once on your PostgreSQL database.

-- 1) Ensure products table has ownership metadata columns.
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS role VARCHAR(20);

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS owner_user_id BIGINT;

-- 2) Backfill existing products as ADMIN-owned.
UPDATE products
SET role = 'ADMIN'
WHERE role IS NULL;

-- 3) Enforce non-null role for new and existing rows.
ALTER TABLE products
    ALTER COLUMN role SET NOT NULL;

-- 4) Add FK from products.owner_user_id -> users.id (optional for legacy rows).
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_products_owner_user'
          AND table_name = 'products'
    ) THEN
        ALTER TABLE products
            ADD CONSTRAINT fk_products_owner_user
            FOREIGN KEY (owner_user_id)
            REFERENCES users(id);
    END IF;
END $$;

-- 5) Helpful index for seller product queries.
CREATE INDEX IF NOT EXISTS idx_products_owner_user_id ON products(owner_user_id);
