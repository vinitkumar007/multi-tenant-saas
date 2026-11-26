package com.saas.tenant.exception;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(String msg) {
        super(msg);
    }
}