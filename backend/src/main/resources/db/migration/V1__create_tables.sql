-- ============================================================
-- V1: Core schema for Keystone Field Service Management System
-- ============================================================

-- ---------- customers ----------
CREATE TABLE customers (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(200) NOT NULL,
    contact_info VARCHAR(500),
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------- sites ----------
CREATE TABLE sites (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES customers(id),
    name        VARCHAR(200) NOT NULL,
    address     VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_sites_customer_id ON sites(customer_id);

-- ---------- users ----------
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(200) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30) NOT NULL
        CHECK (role IN ('DISPATCHER', 'TECHNICIAN', 'MANAGER', 'CUSTOMER')),
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------- parts ----------
CREATE TABLE parts (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(200) NOT NULL,
    sku            VARCHAR(100) NOT NULL UNIQUE,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    unit_cost      NUMERIC(12, 2) NOT NULL DEFAULT 0,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);

-- ---------- work_orders ----------
CREATE TABLE work_orders (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                   VARCHAR(50) NOT NULL UNIQUE,
    title                  VARCHAR(200) NOT NULL,
    description            TEXT,
    priority               VARCHAR(20) NOT NULL
        CHECK (priority IN ('CRITICAL', 'HIGH', 'MEDIUM', 'LOW')),
    status                 VARCHAR(20) NOT NULL
        CHECK (status IN ('NEW', 'ASSIGNED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CLOSED', 'CANCELLED')),
    sla_due_at             TIMESTAMP,
    customer_id            UUID NOT NULL REFERENCES customers(id),
    site_id                UUID NOT NULL REFERENCES sites(id),
    assigned_technician_id UUID REFERENCES users(id),
    created_at             TIMESTAMP NOT NULL DEFAULT now(),
    updated_at             TIMESTAMP NOT NULL DEFAULT now(),
    closed_at              TIMESTAMP
);
CREATE INDEX idx_work_orders_customer_id ON work_orders(customer_id);
CREATE INDEX idx_work_orders_site_id ON work_orders(site_id);
CREATE INDEX idx_work_orders_assigned_technician_id ON work_orders(assigned_technician_id);
CREATE INDEX idx_work_orders_status ON work_orders(status);

-- ---------- work_order_status_history (append-only) ----------
CREATE TABLE work_order_status_history (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_order_id UUID NOT NULL REFERENCES work_orders(id),
    from_status   VARCHAR(20)
        CHECK (from_status IN ('NEW', 'ASSIGNED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CLOSED', 'CANCELLED')),
    to_status     VARCHAR(20) NOT NULL
        CHECK (to_status IN ('NEW', 'ASSIGNED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CLOSED', 'CANCELLED')),
    changed_by    UUID NOT NULL REFERENCES users(id),
    note          VARCHAR(500),
    changed_at    TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_wo_status_history_work_order_id ON work_order_status_history(work_order_id);

-- ---------- part_usage ----------
CREATE TABLE part_usage (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_order_id UUID NOT NULL REFERENCES work_orders(id),
    part_id       UUID NOT NULL REFERENCES parts(id),
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    logged_at     TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_part_usage_work_order_id ON part_usage(work_order_id);
CREATE INDEX idx_part_usage_part_id ON part_usage(part_id);

-- ---------- time_logs ----------
CREATE TABLE time_logs (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    work_order_id  UUID NOT NULL REFERENCES work_orders(id),
    technician_id  UUID NOT NULL REFERENCES users(id),
    minutes        INTEGER NOT NULL CHECK (minutes > 0),
    logged_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_time_logs_work_order_id ON time_logs(work_order_id);
CREATE INDEX idx_time_logs_technician_id ON time_logs(technician_id);