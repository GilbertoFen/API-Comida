package com.demoapi.apicomida.util;

import java.util.Map;

public final class ApiResponses {

    private ApiResponses() {
    }

    public static Map<String, String> message(String message) {
        return Map.of("message", message);
    }
}
