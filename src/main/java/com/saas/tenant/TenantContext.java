package com.saas.tenant;

/**
 * ThreadLocal holder — current request ka tenant id store karta hai.
 * Har HTTP request apne thread pe chalti hai, isliye ThreadLocal perfect hai.
 * Request end pe MUST clear karna hai (memory leak se bachne ke liye).
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}