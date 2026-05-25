package org.jpa.gettingalongwithjpa.jpa.lifecycle;

import org.jpa.gettingalongwithjpa.domain.Library;
import org.jpa.gettingalongwithjpa.jpa.support.JpaExplorationTestBase;
import org.jpa.gettingalongwithjpa.jpa.support.JpaTxTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(JpaTxTestConfig.class)
class DirtyCheckingLifecycleTest extends JpaExplorationTestBase {

    @Test
    void modifyDetachedEntity_thenFlush_doesNotPersistChanges() {
        Library saved = libraries.save(library("before"));
        Long id = saved.getId();

        Library detached = libraries.findById(id).orElseThrow();
        detached.setName("after");

        // Flushing a new persistence context does nothing for a detached instance.
        tx.execute(status -> {
            em.flush();
            return null;
        });

        assertEquals("before", libraries.findById(id).orElseThrow().getName());
    }

    @Test
    void modifyManagedEntityWithinOuterTransaction_thenFlush_persistsChanges() {
        Library saved = libraries.save(library("before"));
        Long id = saved.getId();

        tx.execute(status -> {
            Library managed = libraries.findById(id).orElseThrow();
            managed.setName("after");
            em.flush();
            return null;
        });

        assertEquals("after", libraries.findById(id).orElseThrow().getName());
        assertTrue(libraries.findById(id).isPresent());
    }
}

