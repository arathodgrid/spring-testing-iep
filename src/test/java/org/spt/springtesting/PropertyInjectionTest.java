package org.spt.springtesting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.spt.springtesting.service.MessageService;

@SpringBootTest
@TestPropertySource(properties = {
        "app.message=Hello From Test"
})
class PropertyInjectionTest {

    @Autowired
    private MessageService messageService;

    @Test
    void testInjectedProperty() {

        Assertions.assertEquals(
                "Hello From Test",
                messageService.getMessage()
        );
    }
}
