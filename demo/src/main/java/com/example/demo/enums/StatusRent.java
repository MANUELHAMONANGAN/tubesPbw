package com.example.demo.enums;

public enum StatusRent {
    ONGOING("ongoing"),
    DONE("done");

    private final String value;

    StatusRent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static StatusRent fromString(String status) {
        for (StatusRent s : StatusRent.values()) {
            if (s.value.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }
}
