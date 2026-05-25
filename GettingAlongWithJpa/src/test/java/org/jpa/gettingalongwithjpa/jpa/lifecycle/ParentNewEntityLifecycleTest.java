package org.jpa.gettingalongwithjpa.jpa.lifecycle;

import org.jpa.gettingalongwithjpa.domain.Library;
import org.jpa.gettingalongwithjpa.jpa.support.JpaExplorationTestBase;
import org.jpa.gettingalongwithjpa.jpa.support.JpaTxTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(JpaTxTestConfig.class)
class ParentNewEntityLifecycleTest extends JpaExplorationTestBase {

    @Test
    void repositorySave_parentWithoutId_persistsAndAssignsId() {
        Library lib = library("repo-no-id");

        Library saved = libraries.save(lib);

        assertNotNull(saved.getId());
        assertEquals(1L, libraries.count());
    }

    @Test
    void entityManagerPersist_parentWithoutId_persistsAndAssignsIdOnFlush() {
        Library lib = library("persist-no-id");

        Long id = tx.execute(status -> {
            em.persist(lib);
            em.flush();
            return lib.getId();
        });

        assertNotNull(id);
        assertEquals(1L, libraries.count());
    }

    @Test
    void entityManagerMerge_parentWithoutId_returnsManagedCopy_originalStaysTransient() {
        Library lib = library("merge-no-id");

        Long mergedId = tx.execute(status -> {
            Library managedCopy = em.merge(lib);
            em.flush();

            assertNull(lib.getId(), "merge() returns a managed copy; the passed instance stays detached/transient");
            assertNotNull(managedCopy.getId());
            return managedCopy.getId();
        });

        assertNotNull(mergedId);
        assertEquals(1L, libraries.count());
    }
}

