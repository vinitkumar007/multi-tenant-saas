package com.saas.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Request aaye → header / subdomain se tenant id nikalo → TenantContext me set karo.
 *
 * Tenant identify order:
 *   1. Header:  X-Tenant-Id
 *   2. Subdomain: acme.myapp.com → "acme"
 *   3. Skip (login/register/public endpoints)
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId == null || tenantId.isBlank()) {
            tenantId = resolveFromSubdomain(request);
        }

        if (tenantId != null && !tenantId.isBlank()) {
            TenantContext.setTenantId(tenantId);
            MDC.put("tenantId", tenantId);   // logs me tenant id dikhega
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 🔴 MUST — warna thread pool me leak ho jaayega tenant id
        TenantContext.clear();
        MDC.remove("tenantId");
    }

    private String resolveFromSubdomain(HttpServletRequest req) {
        String host = req.getServerName(); // acme.myapp.com
        if (host == null) return null;

        String[] parts = host.split("\\.");
        if (parts.length >= 3 && !parts[0].equals("www")) {
            return parts[0].toLowerCase();
        }
        return null;
    }
}