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
class ParentWithChildrenLifecycleTest extends JpaExplorationTestBase {

    @Test
    void repositorySave_parentWithNewChildren_cascadesPersist() {
        Library lib = library("lib");
        lib.addBook(book("b1"));
        lib.addBook(book("b2"));

        libraries.saveAndFlush(lib);

        assertEquals(1L, libraries.count());
        assertEquals(2L, books.count());
    }

    @Test
    void entityManagerPersist_parentWithNewChildren_cascadesPersist() {
        Library lib = library("lib");
        lib.addBook(book("b1"));
        lib.addBook(book("b2"));

        tx.execute(status -> {
            em.persist(lib);
            em.flush();
            return null;
        });

        assertEquals(1L, libraries.count());
        assertEquals(2L, books.count());
    }

    @Test
    void entityManagerMerge_parentWithNewChildren_cascadesPersist_butReturnsCopy() {
        Library lib = library("lib");
        lib.addBook(book("b1"));
        lib.addBook(book("b2"));

        tx.execute(status -> {
            Library merged = em.merge(lib);
            em.flush();
            assertNull(lib.getId());
            assertNotNull(merged.getId());
            return null;
        });

        assertEquals(1L, libraries.count());
        assertEquals(2L, books.count());
    }

    @Test
    void repositorySave_newParentWithExistingDetachedChildren_failsBecauseCascadePersistHitsDetachedChildren() {
        Book b1 = books.save(book("existing-1"));
        Book b2 = books.save(book("existing-2"));

        Library lib = library("lib");
        lib.addBook(b1);
        lib.addBook(b2);

        assertThrows(RuntimeException.class, () -> libraries.saveAndFlush(lib));
    }

    @Test
    void entityManagerPersist_newParentWithExistingDetachedChildren_failsForSameReason() {
        Book b1 = books.save(book("existing-1"));
        Book b2 = books.save(book("existing-2"));

        Library lib = library("lib");
        lib.addBook(b1);
        lib.addBook(b2);

        assertThrows(RuntimeException.class, () ->
                tx.execute(status -> {
                    em.persist(lib);
                    em.flush();
                    return null;
                })
        );
    }

    @Test
    void entityManagerMerge_newParentWithExistingDetachedChildren_usuallyWorks() {
        Book b1 = books.save(book("existing-1"));
        Book b2 = books.save(book("existing-2"));

        Library lib = library("lib");
        lib.addBook(b1);
        lib.addBook(b2);

        tx.execute(status -> {
            em.merge(lib);
            em.flush();
            return null;
        });

        assertEquals(1L, libraries.count());
        assertEquals(2L, books.count());
        assertTrue(books.findAll().stream().allMatch(b -> b.getLibrary() != null));
    }
}

