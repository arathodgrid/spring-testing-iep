package org.jpa.gettingalongwithjpa;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DirtyContextTest {

    private static String sharedState = "INITIAL";

    @Test
    @Order(1)
    void firstTestChangesSharedState() {

        sharedState = "MODIFIED";

        Assertions.assertEquals(
                "MODIFIED",
                sharedState
        );
    }

    @Test
    @Order(2)
    void secondTestFailsBecauseContextIsDirty() {

        // This test will fail

        Assertions.assertEquals(
                "INITIAL",
                sharedState
        );
    }
}