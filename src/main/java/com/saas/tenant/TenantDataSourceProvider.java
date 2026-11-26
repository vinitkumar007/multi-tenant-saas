package com.saas.tenant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Har naye tenant ke liye lazy Hikari pool banata hai.
 */
@Component
public class TenantDataSourceProvider {

    @Value("${spring.datasource.tenant-template.url}")
    private String tenantUrlTemplate;

    @Value("${spring.datasource.tenant-template.username}")
    private String username;

    @Value("${spring.datasource.tenant-template.password}")
    private String password;

    @Value("${spring.datasource.tenant-template.driver-class-name}")
    private String driverClass;

    public DataSource createDataSourceForTenant(String schema) {
        String url = tenantUrlTemplate.replace("{schema}", schema);

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(username);
        cfg.setPassword(password);
        cfg.setDriverClassName(driverClass);
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(1);
        cfg.setPoolName("tenant-" + schema + "-pool");
        cfg.setConnectionTimeout(20_000);
        cfg.setInitializationFailTimeout(-1);

        return new HikariDataSource(cfg);
    }
}