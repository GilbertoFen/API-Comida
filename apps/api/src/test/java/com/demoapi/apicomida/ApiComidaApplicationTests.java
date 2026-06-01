package com.demoapi.apicomida;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

class ApiComidaApplicationTests {

    @Test
    void contextLoads() {
        try (ConfigurableApplicationContext ignored = new SpringApplicationBuilder(ApiComidaApplication.class)
                .properties("spring.main.web-application-type=none")
                .run()) {
        }
    }
}
