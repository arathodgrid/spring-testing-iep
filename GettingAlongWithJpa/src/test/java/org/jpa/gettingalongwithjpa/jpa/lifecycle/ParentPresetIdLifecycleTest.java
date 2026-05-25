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
class ParentPresetIdLifecycleTest extends JpaExplorationTestBase {

    @Test
    void repositorySave_parentWithPresetIdAndNoRow_mayFail() {
        Library lib = library("repo-with-id");
        lib.setId(42L);

        assertThrows(RuntimeException.class, () -> libraries.saveAndFlush(lib));
    }

    @Test
    void entityManagerPersist_parentWithPresetId_throws() {
        Library lib = library("persist-with-id");
        lib.setId(43L);

        assertThrows(RuntimeException.class, () ->
                tx.execute(status -> {
                    em.persist(lib);
                    em.flush();
                    return null;
                })
        );
    }

    @Test
    void entityManagerMerge_parentWithPresetIdAndNoRow_mayFail() {
        Library lib = library("merge-with-id");
        lib.setId(44L);

        assertThrows(RuntimeException.class, () ->
                tx.execute(status -> {
                    em.merge(lib);
                    em.flush();
                    return null;
                })
        );
    }

    @Test
    void repositorySave_sameId_updatesExistingRow() {
        Library inserted = libraries.save(library("original"));
        Long id = inserted.getId();

        Library another = library("updated-by-repo");
        another.setId(id);
        libraries.saveAndFlush(another);

        assertEquals(1L, libraries.count());
        assertEquals("updated-by-repo", libraries.findById(id).orElseThrow().getName());
    }

    @Test
    void entityManagerPersist_sameId_throws() {
        Library inserted = libraries.save(library("original"));
        Long id = inserted.getId();

        Library another = library("persist-same-id");
        another.setId(id);

        assertThrows(RuntimeException.class, () ->
                tx.execute(status -> {
                    em.persist(another);
                    em.flush();
                    return null;
                })
        );
    }

    @Test
    void entityManagerMerge_sameId_updatesExistingRow() {
        Library inserted = libraries.save(library("original"));
        Long id = inserted.getId();

        Library another = library("updated-by-merge");
        another.setId(id);

        tx.execute(status -> {
            em.merge(another);
            em.flush();
            return null;
        });

        assertEquals(1L, libraries.count());
        assertEquals("updated-by-merge", libraries.findById(id).orElseThrow().getName());
    }
}

