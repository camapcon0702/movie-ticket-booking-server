package qnt.moviebooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import qnt.moviebooking.entity.GenreEntity;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    List<GenreEntity> findAllByDeletedAtIsNull();

    Optional<GenreEntity> findByNameAndDeletedAtIsNull(String name);

    Optional<GenreEntity> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<GenreEntity> findAllByDeletedAtBefore(LocalDateTime dateTime);
}
