package org.jpa.gettingalongwithjpa.jpa.lifecycle;

import org.jpa.gettingalongwithjpa.domain.Book;
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
class ChildLifecycleTest extends JpaExplorationTestBase {

    @Test
    void repositorySave_childWithoutParent_persists() {
        Book saved = books.saveAndFlush(book("orphan"));
        assertNotNull(saved.getId());
        assertNull(books.findById(saved.getId()).orElseThrow().getLibrary());
    }

    @Test
    void entityManagerPersist_childWithoutParent_persists() {
        Book b = book("orphan");

        tx.execute(status -> {
            em.persist(b);
            em.flush();
            return null;
        });

        assertEquals(1L, books.count());
    }

    @Test
    void entityManagerMerge_childWithoutParent_persistsButReturnsCopy() {
        Book b = book("orphan");

        tx.execute(status -> {
            Book merged = em.merge(b);
            em.flush();
            assertNull(b.getId());
            assertNotNull(merged.getId());
            return null;
        });

        assertEquals(1L, books.count());
    }

    @Test
    void repositorySave_childWithTransientParent_failsWithoutCascadeFromChildToParent() {
        Library parent = library("transient-parent");
        Book child = book("child");
        child.setLibrary(parent);

        assertThrows(RuntimeException.class, () -> books.saveAndFlush(child));
    }

    @Test
    void entityManagerPersist_childWithTransientParent_failsWithoutCascadeFromChildToParent() {
        Library parent = library("transient-parent");
        Book child = book("child");
        child.setLibrary(parent);

        assertThrows(RuntimeException.class, () ->
                tx.execute(status -> {
                    em.persist(child);
                    em.flush();
                    return null;
                })
        );
    }

    @Test
    void entityManagerMerge_childWithTransientParent_alsoFailsWithoutCascade() {
        Library parent = library("transient-parent");
        Book child = book("child");
        child.setLibrary(parent);

        assertThrows(RuntimeException.class, () ->
                tx.execute(status -> {
                    em.merge(child);
                    em.flush();
                    return null;
                })
        );
    }

    @Test
    void childWithExistingParentButDetached_repositorySave_worksWhenFkIsKnown() {
        Library persisted = libraries.save(library("parent"));
        Long parentId = persisted.getId();

        Book child = book("child");
        child.setLibrary(persisted); // detached reference (different tx)

        books.saveAndFlush(child);

        assertEquals(1L, libraries.count());
        assertEquals(1L, books.count());
        assertEquals(parentId, books.findAll().get(0).getLibrary().getId());
    }

    @Test
    void childWithExistingParentButDetached_entityManagerPersist_worksWhenFkIsKnown() {
        Library persisted = libraries.save(library("parent"));
        Long parentId = persisted.getId();
        em.clear();

        Book child = book("child");
        child.setLibrary(persisted);

        tx.execute(status -> {
            em.persist(child);
            em.flush();
            return null;
        });

        assertEquals(parentId, books.findAll().get(0).getLibrary().getId());
    }

    @Test
    void childWithExistingParentButDetached_entityManagerMerge_worksAndAttachesGraph() {
        Library persisted = libraries.save(library("parent"));
        Long parentId = persisted.getId();
        em.clear();

        Book child = book("child");
        child.setLibrary(persisted);

        tx.execute(status -> {
            Book merged = em.merge(child);
            em.flush();
            assertNotNull(merged.getId());
            assertEquals(parentId, merged.getLibrary().getId());
            return null;
        });

        assertEquals(parentId, books.findAll().get(0).getLibrary().getId());
    }
}

