CREATE TABLE IF NOT EXISTS tenants (
    id           UUID PRIMARY KEY,
    schema_name  VARCHAR(63)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    subdomain    VARCHAR(63)  NOT NULL,
    owner_email  VARCHAR(255) NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    CONSTRAINT uk_tenant_schema    UNIQUE (schema_name),
    CONSTRAINT uk_tenant_subdomain UNIQUE (subdomain)
);

CREATE INDEX IF NOT EXISTS idx_tenants_subdomain ON tenants(subdomain);
CREATE INDEX IF NOT EXISTS idx_tenants_status    ON tenants(status);