package com.example.demo.enums;

public enum MethodBayarEnum {
    TUNAI("Tunai"),
    NON_TUNAI("Non-Tunai");

    private final String value;

    MethodBayarEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static MethodBayarEnum fromString(String method) {
        for (MethodBayarEnum m : MethodBayarEnum.values()) {
            if (m.value.equalsIgnoreCase(method)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Invalid payment method: " + method);
    }
}
