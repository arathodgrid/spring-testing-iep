package org.jpa.gettingalongwithjpa;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CleanContextTest {

    private static String sharedState = "INITIAL";

    @Test
    @Order(1)
    @DirtiesContext
    void firstTestChangesSharedState() {

        sharedState = "MODIFIED";

        Assertions.assertEquals(
                "MODIFIED",
                sharedState
        );
    }

    @Test
    @Order(2)
    void secondTestWorksCorrectly() {

        sharedState = "INITIAL";

        Assertions.assertEquals(
                "INITIAL",
                sharedState
        );
    }
}