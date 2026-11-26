package com.saas.tenant;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AbstractRoutingDataSource extend karta hai.
 * determineCurrentLookupKey() se decide hota hai kaunsa DataSource use karna hai.
 *
 * Agar TenantContext me tenantId set hai  → us tenant ka DB
 * Agar nahi set hai (e.g. login, register)  → master DB
 */
@Setter
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(TenantRoutingDataSource.class);

    private DataSource masterDataSource;
    private TenantDataSourceProvider provider;

    // Cache of already-created tenant datasources (thread-safe)
    private final Map<Object, DataSource> tenantCache = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() {
        // default + targetDataSources set kar diye config me
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            log.debug("No tenant in context → routing to MASTER");
            return null; // default = master
        }
        return tenantId;
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();

        // Null → master DB
        if (lookupKey == null) {
            return masterDataSource;
        }

        String tenantId = lookupKey.toString();

        // Already cached? return it
        DataSource existing = tenantCache.get(tenantId);
        if (existing != null) {
            return existing;
        }

        // Lazy create — pehli baar is tenant ka datasource ban raha hai
        synchronized (this) {
            existing = tenantCache.get(tenantId);
            if (existing != null) return existing;

            DataSource ds = provider.createDataSourceForTenant(tenantId);
            tenantCache.put(tenantId, ds);
            log.info("Created new DataSource for tenant [{}]", tenantId);
            return ds;
        }
    }

    /** Tenant delete hone pe cache se hatao */
    public void evictTenant(String tenantId) {
        DataSource ds = tenantCache.remove(tenantId);
        if (ds instanceof AutoCloseable ac) {
            try { ac.close(); } catch (Exception ignored) {}
        }
    }
}