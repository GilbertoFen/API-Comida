package com.demoapi.apicomida.util;

import java.util.Arrays;

public enum Unit {
    GR(0, "gr"),
    OZ(1, "oz"),
    KG(2, "kg"),
    LT(3, "lt"),
    ML(4, "ml");

    private int id;
    private String name;
    Unit(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public static String getUnit(int id) {
        return Arrays.stream(Unit.values()).filter(u -> u.getId() == id).findFirst().get().getName();
    }
}
