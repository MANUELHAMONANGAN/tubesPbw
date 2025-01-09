package com.example.demo.enums;

public enum RentEnum {
    PINJAM("Pinjam"),
    PENGEMBALIAN("Pengembalian");

    private final String value;

    RentEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static RentEnum fromString(String type) {
        for (RentEnum r : RentEnum.values()) {
            if (r.value.equalsIgnoreCase(type)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid transaction type: " + type);
    }
}
