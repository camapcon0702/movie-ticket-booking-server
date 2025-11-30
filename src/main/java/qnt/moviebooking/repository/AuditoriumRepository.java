package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.AuditoriumEntity;

import java.util.Optional;

@Repository
public interface AuditoriumRepository extends JpaRepository<AuditoriumEntity, Long> {

    Optional<AuditoriumEntity> findByIdAndDeletedAtIsNull(Long id);
}
