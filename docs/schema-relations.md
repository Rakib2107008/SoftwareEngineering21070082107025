# Schema Relations and Cardinality (MobileZBD)

This file explains relationship type, cardinality, and key ownership for the current schema.

## Tables Covered
- users
- customer_accounts
- product_details
- products
- product_sell_history

## Relationship Summary

1. users -> customer_accounts
- Relation type: one-to-one (logical)
- Cardinality: one user has zero or one customer account; one customer account belongs to exactly one user
- FK: customer_accounts.user_id -> users.id
- Why one-to-one: `user_id` is marked UNIQUE in mapping
- JPA mapping:
  - User side: `@OneToOne(mappedBy = "user")`
  - CustomerAccount side: `@OneToOne @JoinColumn(name = "user_id", unique = true)`

2. product_details -> customer_accounts
- Relation type: one-to-many
- Cardinality: one product_details row can be referenced by many customer_accounts rows
- FK: customer_accounts.product_id -> product_details.id
- Optionality: product_id is nullable in mapping (account may not reference a product)
- JPA mapping:
  - CustomerAccount: `@ManyToOne @JoinColumn(name = "product_id")`

3. product_details -> products
- Relation type: one-to-many
- Cardinality: one product_details row can have many products rows
- FK: products.product_details_id -> product_details.id
- Optionality: required (non-null)
- JPA mapping:
  - ProductDetails: `@OneToMany(mappedBy = "productDetails")`
  - Products: `@ManyToOne @JoinColumn(name = "product_details_id", nullable = false)`

4. users -> products (owner)
- Relation type: one-to-many
- Cardinality: one user can own many products rows; each products row has at most one owner user
- FK: products.owner_user_id -> users.id
- Optionality: owner_user_id is nullable in entity mapping
- JPA mapping:
  - Products: `@ManyToOne @JoinColumn(name = "owner_user_id")`
- DB note:
  - Manual migration adds foreign key constraint and index on owner_user_id

5. customer_accounts -> product_sell_history
- Relation type: one-to-many
- Cardinality: one customer account can have many sell history rows
- FK: product_sell_history.customer_id -> customer_accounts.customer_id
- Optionality: required (non-null)
- JPA mapping:
  - CustomerAccount: `@OneToMany(mappedBy = "customer")`
  - ProductSellHistory: `@ManyToOne @JoinColumn(name = "customer_id", nullable = false)`

6. product_details -> product_sell_history
- Relation type: one-to-many
- Cardinality: one product details row can appear in many sell history rows
- FK: product_sell_history.product_id -> product_details.id
- Optionality: required (non-null)
- JPA mapping:
  - ProductDetails: `@OneToMany(mappedBy = "product")`
  - ProductSellHistory: `@ManyToOne @JoinColumn(name = "product_id", nullable = false)`

## Enum / Domain Types Used in Security and Product Ownership

1. users.role
- Type: UserRole enum
- Values: ROLE_ADMIN, ROLE_CUSTOMER, ROLE_SELLER

2. product_details.category
- Type: ProductCategory enum
- Values: SMARTPHONE, TABLET, SMART_TV, ADAPTER, LAPTOP, ACCESSORIES, IPAD, AIRPOD, POWER_BANK, HEADPHONES

3. products.role
- Type: ProductOwnerRole enum
- Values: ADMIN, SELLER

## Important Modeling Notes

1. Historical table denormalization
- product_sell_history stores both FK references and snapshot text fields:
  - customer_name
  - product_name
  - category
  - color
- This preserves historical values even if referenced master data changes later.

2. customer_accounts.product_id meaning
- customer_accounts has a ManyToOne link to product_details, which is unusual for a pure account table.
- If the intent is customer profile only, this relation may represent legacy or temporary coupling.

3. Image column type normalization
- Runtime schema fix ensures `image` columns are TEXT in:
  - product_details.image
  - products.image

## PlantUML Source
- ER diagram source file: docs/er-diagram.puml
