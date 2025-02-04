package com.demoapi.apicomida.util;

import java.util.Arrays;

public enum Exercise {
    LOW(0, "low"),
    MODERATE(1, "moderate"),
    HIGH(2, "high"),
    INTENSE(3, "intense");

    private final int id;
    private final String name;

    Exercise(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public static String getLevel(int id) {
        return Arrays.stream(Exercise.values())
                .filter(e -> e.getId() == id)
                .findFirst()
                .map(Exercise::getName)
                .orElseThrow(() -> new IllegalArgumentException("ID de ejercicio inválido: " + id));
    }

    public static Exercise getById(int id) {
        return Arrays.stream(Exercise.values())
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ID de ejercicio inválido: " + id));
    }
}
