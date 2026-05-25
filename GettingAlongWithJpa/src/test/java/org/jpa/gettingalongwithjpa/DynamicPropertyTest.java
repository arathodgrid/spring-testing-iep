package org.jpa.gettingalongwithjpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class DynamicPropertyTest {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        registry.add(
                "app.message",
                () -> "Dynamic Message"
        );
    }

    @Test
    void contextLoads() {

    }
}