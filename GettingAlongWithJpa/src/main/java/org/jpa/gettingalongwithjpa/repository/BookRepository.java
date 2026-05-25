package org.jpa.gettingalongwithjpa.repository;

import org.jpa.gettingalongwithjpa.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}

