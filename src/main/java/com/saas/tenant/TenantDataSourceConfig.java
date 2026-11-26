package com.saas.tenant;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TenantDataSourceConfig {

    /**
     * Master datasource — tenants/users/subscriptions yahan rehte hain.
     */
    @Bean(name = "masterDataSource")
    @ConfigurationProperties("spring.datasource.master")
    public DataSource masterDataSource() {
        return new HikariDataSource();
    }

    /**
     * Template properties — naya tenant datasource banane ke liye use honge.
     */
    @Bean(name = "tenantTemplateDataSource")
    @ConfigurationProperties("spring.datasource.tenant-template")
    public DataSource tenantTemplateDataSource() {
        return new HikariDataSource();
    }

    /**
     * Routing datasource — sabse important bean.
     * Spring isi ko PRIMARY datasource maanega aur saari JPA queries yahan se jaayengi.
     * Runtime pe decide karega kaunsa tenant DB hit karna hai.
     */
    @Bean(name = "routingDataSource")
    @Primary
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            @Qualifier("tenantTemplateDataSource") DataSource tenantTemplateDataSource,
            TenantDataSourceProvider provider) {

        TenantRoutingDataSource routing = new TenantRoutingDataSource();
        routing.setTargetDataSources(new HashMap<>());
        routing.setDefaultTargetDataSource(masterDataSource);
        routing.setMasterDataSource(masterDataSource);
        routing.setProvider(provider);   // provider lazily tenant DBs banayega
        return routing;
    }
}