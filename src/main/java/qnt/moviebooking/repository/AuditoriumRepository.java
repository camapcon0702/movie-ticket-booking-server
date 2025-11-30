package qnt.moviebooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import qnt.moviebooking.entity.AuditoriumEntity;

@Repository
public interface AuditoriumRepository extends JpaRepository<AuditoriumEntity, Long> {
    Optional<AuditoriumEntity> findByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndDeletedAtIsNull(String name);

    Optional<AuditoriumEntity> findByIdAndDeletedAtIsNull(Long id);

    List<AuditoriumEntity> findByDeletedAtIsNull();

    List<AuditoriumEntity> findAllByDeletedAtAfter(LocalDateTime time);
}
