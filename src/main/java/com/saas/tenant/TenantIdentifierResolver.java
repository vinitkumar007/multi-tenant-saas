package com.saas.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Hibernate ko batata hai current tenant kaun hai.
 * (Multi-tenant strategy me Hibernate cache segregation ke liye useful)
 * Filhaal hum sirf routing pe depend kar rahe hain, but resolver
 * add karna Hibernate 2nd-level cache ke liye safe hai.
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    private static final String DEFAULT_TENANT = "master";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getTenantId();
        return tenant != null ? tenant : DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}