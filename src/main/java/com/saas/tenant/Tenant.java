package com.saas.tenant;

import com.saas.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tenants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_schema", columnNames = "schema_name"),
        @UniqueConstraint(name = "uk_tenant_subdomain", columnNames = "subdomain")
})
public class Tenant extends BaseEntity {

    @Column(name = "schema_name", nullable = false, length = 63)
    private String schemaName;          // e.g. "acme"

    @Column(nullable = false, length = 100)
    private String name;                // display name: "Acme Corp"

    @Column(nullable = false, length = 63)
    private String subdomain;           // "acme" → acme.myapp.com

    @Column(name = "owner_email", nullable = false, length = 255)
    private String ownerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantStatus status = TenantStatus.ACTIVE;

    public enum TenantStatus { ACTIVE, SUSPENDED, DELETED }
}