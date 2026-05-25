package org.jpa.gettingalongwithjpa.repository;

import org.jpa.gettingalongwithjpa.domain.Library;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, Long> {
}

