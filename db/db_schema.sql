-- 1. Users (Customers + Service Professionals + Admins)

CREATE TABLE pan_info (
    pan_hash           VARCHAR(32)      PRIMARY KEY,
    pan_ref_id         VARCHAR(64)      UNIQUE NOT NULL,
    status             VARCHAR(16)      NOT NULL,
    created_date       TIMESTAMP        NOT NULL,
    last_accessed_date TIMESTAMP        ,
    access_count       BIGINT           NOT NULL
);

CREATE TABLE fks_users (
    user_id            BIGINT           PRIMARY KEY,
    full_name          VARCHAR(96)      NOT NULL,
    email              VARCHAR(128)     NOT NULL,
    phone1             VARCHAR(20)      NOT NULL,
    phone2             VARCHAR(20)      ,
    password_hash      TEXT             NOT NULL,
    role               VARCHAR(32)      CHECK (role IN ('CUSTOMER','PROFESSIONAL','ADMIN')),
    status             VARCHAR(32)      CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED')),
    created_at         TIMESTAMP        NOT NULL,
    updated_at         TIMESTAMP
);

-- Address
CREATE TABLE fks_addresses (
    address_id         BIGINT           PRIMARY KEY,
    user_id            BIGINT           NOT NULL,
    address_line1      VARCHAR(255)     NOT NULL,
    address_line2      VARCHAR(255)     NOT NULL,
    city               VARCHAR(64)      NOT NULL,
    state              VARCHAR(64)      NOT NULL,
    pincode            VARCHAR(20)      NOT NULL,
    latitude           DECIMAL(10,7)    ,
    longitude          DECIMAL(10,7)    ,
    is_default         SMALLINT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES fks_users(user_id)
);

-- 2. Service Catalog - Categories & Services
-- Categories and Services

CREATE TABLE fks_categories (
    category_id        BIGINT           PRIMARY KEY,
    name               VARCHAR(128)     NOT NULL,
    parent_id          BIGINT           ,
    FOREIGN KEY (parent_id) REFERENCES fks_categories(category_id)
);

CREATE TABLE fks_services (
    service_id         BIGINT            PRIMARY KEY,
    category_id        BIGINT            NOT NULL,
    name               VARCHAR(128)      NOT NULL,
    description        TEXT              ,
    base_price         DECIMAL(10,2)     NOT NULL,
    duration_minutes   INT               ,
    FOREIGN KEY (category_id) REFERENCES fks_categories(category_id)
);

-- Professional Profiles
CREATE TABLE fks_professionals (
    professional_id    BIGINT           PRIMARY KEY,
    user_id            BIGINT           NOT NULL,
    bio                TEXT             ,
    experience_years   INT              NOT NULL,
    rating_avg         DECIMAL(3,2)     ,
    is_verified        SMALLINT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES fks_users(user_id)
);

-- Professional Skills
CREATE TABLE fks_professional_services (
    id                 BIGINT           PRIMARY KEY,
    professional_id    BIGINT           NOT NULL,
    service_id         BIGINT           NOT NULL,
    price              DECIMAL(10,2)    NOT NULL,
    is_active          SMALLINT         NOT NULL,
    FOREIGN KEY (professional_id) REFERENCES fks_professionals(professional_id),
    FOREIGN KEY (service_id) REFERENCES fks_services(service_id)
);

-- 3. Booking & Scheduling

-- Booking
CREATE TABLE fks_bookings (
    booking_id         BIGINT           PRIMARY KEY,
    customer_id        BIGINT           NOT NULL,
    professional_id    BIGINT           NOT NULL,
    service_id         BIGINT           NOT NULL,
    address_id         BIGINT           NOT NULL,
    scheduled_at       TIMESTAMP        NOT NULL,
    status             VARCHAR(32)      CHECK (status IN ('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED')),
    total_amount       DECIMAL(10,2)    NOT NULL,
    created_at         TIMESTAMP        NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES fks_users(user_id),
    FOREIGN KEY (professional_id) REFERENCES fks_professionals(professional_id),
    FOREIGN KEY (service_id) REFERENCES fks_services(service_id),
    FOREIGN KEY (address_id) REFERENCES fks_addresses(address_id)
);

-- Time Slots / Availability
CREATE TABLE fks_availability (
    availability_id    BIGINT           PRIMARY KEY,
    professional_id    BIGINT           NOT NULL,
    date               DATE             NOT NULL,
    start_time         TIMESTAMP        ,
    end_time           TIMESTAMP        ,
    is_booked          SMALLINT         NOT NULL,
    FOREIGN KEY (professional_id) REFERENCES fks_professionals(professional_id)
);

-- 4. Payments & Pricing

-- Payments
CREATE TABLE fks_payments (
    payment_id         BIGINT           PRIMARY KEY,
    booking_id         BIGINT           NOT NULL,
    amount             DECIMAL(10,2)    NOT NULL,
    payment_method     VARCHAR(32)      CHECK (payment_method IN ('CARD','UPI','WALLET','COD')),
    payment_status     VARCHAR(32)      CHECK (payment_status IN ('INITIATED','SUCCESS','FAILED','REFUNDED')),
    transaction_ref    VARCHAR(128)     NOT NULL,
    paid_at            TIMESTAMP        NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES fks_bookings(booking_id)
);

-- Coupons & Discounts
CREATE TABLE fks_coupons (
    coupon_id          BIGINT           PRIMARY KEY,
    code               VARCHAR(50)      NOT NULL,
    discount_type      VARCHAR(16)      CHECK (discount_type IN ('PERCENT','FLAT')),
    discount_value     DECIMAL(10,2)    ,
    max_discount       DECIMAL(10,2)    ,
    expiry_date        DATE             NOT NULL,
    usage_limit        INT              ,
);

CREATE TABLE fks_coupon_usage (
    id                 BIGINT           PRIMARY KEY,
    coupon_id          BIGINT           NOT NULL,
    user_id            BIGINT           NOT NULL,
    booking_id         BIGINT           NOT NULL,
    used_at            TIMESTAMP        NOT NULL,
    FOREIGN KEY (coupon_id) REFERENCES fks_coupons(coupon_id),
    FOREIGN KEY (user_id) REFERENCES fks_users(user_id),
    FOREIGN KEY (booking_id) REFERENCES fks_bookings(booking_id)
);

-- 5. Ratings & Reviews

CREATE TABLE fks_reviews (
    review_id          BIGINT           PRIMARY KEY,
    booking_id         BIGINT           NOT NULL,
    customer_id        BIGINT           NOT NULL,
    professional_id    BIGINT           NOT NULL,
    rating             INT              CHECK (rating BETWEEN 1 AND 5),
    comment            TEXT             ,
    created_at         TIMESTAMP        NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES fks_bookings(booking_id)
);

-- 6. Communication
CREATE TABLE fks_conversations (
    conversation_id    BIGINT           PRIMARY KEY,
    booking_id         BIGINT           NOT NULL,
    created_at         TIMESTAMP        NOT NULL
);

CREATE TABLE fks_messages (
    message_id         BIGINT           PRIMARY KEY,
    conversation_id    BIGINT           NOT NULL,
    sender_id          BIGINT           NOT NULL,
    message_text       TEXT             ,
    sent_at            TIMESTAMP        NOT NULL,
    FOREIGN KEY (conversation_id) REFERENCES fks_conversations(conversation_id)
);

-- 7. Operations & Logistics
CREATE TABLE fks_job_status (
    log_id             BIGINT           PRIMARY KEY,
    booking_id         BIGINT           NOT NULL,
    status             VARCHAR(32)      NOT NULL,
    updated_at         TIMESTAMP        ,
    updated_by         BIGINT
);

-- 8. Admin & Compliance

-- Documents (KYC, Verification)
CREATE TABLE fks_documents (
    document_id         BIGINT           PRIMARY KEY,
    user_id             BIGINT           NOT NULL,
    document_type       VARCHAR(50)      NOT NULL,
    document_url        TEXT             ,
    verification_status VARCHAR(32)      CHECK (verification_status IN ('PENDING','APPROVED','REJECTED')),
    uploaded_at         TIMESTAMP
);

-- 9. Analytics / Audit

CREATE TABLE fks_audit_logs (
    log_id             BIGINT           PRIMARY KEY,
    user_id            BIGINT           NOT NULL,
    action             VARCHAR(255)     NOT NULL,
    entity_type        VARCHAR(50)      NOT NULL,
    entity_id          BIGINT           NOT NULL,
    created_at         TIMESTAMP        NOT NULL
);

-- 10. Surge Pricing

CREATE TABLE fks_pricing_rules (
    rule_id            BIGINT           PRIMARY KEY,
    service_id         BIGINT           NOT NULL,
    city               VARCHAR(100)     NOT NULL,
    multiplier         DECIMAL(5,2)     NOT NULL,
    start_time         TIMESTAMP        NOT NULL,
    end_time           TIMESTAMP        NOT NULL
);

-- 11. Wallet Systems

CREATE TABLE fks_wallets (
    wallet_id          BIGINT           PRIMARY KEY,
    user_id            BIGINT           NOT NULL,
    balance            DECIMAL(10,2)    
);

CREATE TABLE fks_wallet_transactions (
    txn_id             BIGINT           PRIMARY KEY,
    wallet_id          BIGINT           NOT NULL,
    amount             DECIMAL(10,2)    NOT NULL,
    type               VARCHAR(32)      CHECK (type IN ('CREDIT','DEBIT')),
    created_at         TIMESTAMP        NOT NULL
);
