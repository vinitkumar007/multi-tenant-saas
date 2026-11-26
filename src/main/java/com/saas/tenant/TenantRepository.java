package com.saas.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findBySchemaName(String schemaName);

    Optional<Tenant> findBySubdomain(String subdomain);

    boolean existsBySchemaName(String schemaName);

    boolean existsBySubdomain(String subdomain);
}