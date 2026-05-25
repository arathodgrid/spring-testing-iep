package org.jpa.gettingalongwithjpa.jpa.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jpa.gettingalongwithjpa.domain.Book;
import org.jpa.gettingalongwithjpa.domain.Library;
import org.jpa.gettingalongwithjpa.repository.BookRepository;
import org.jpa.gettingalongwithjpa.repository.LibraryRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Shared wiring + cleanup for the JPA lifecycle exploration tests.
 *
 * Tests intentionally do NOT use @Transactional at the method level.
 * When a transaction is required, use {@link #tx}.
 */
public abstract class JpaExplorationTestBase {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected TransactionTemplate tx;

    @Autowired
    protected LibraryRepository libraries;

    @Autowired
    protected BookRepository books;

    @AfterEach
    void cleanDatabase() {
        books.deleteAllInBatch();
        libraries.deleteAllInBatch();
    }

    protected Library library(String name) {
        return new Library(name);
    }

    protected Book book(String title) {
        return new Book(title);
    }
}

