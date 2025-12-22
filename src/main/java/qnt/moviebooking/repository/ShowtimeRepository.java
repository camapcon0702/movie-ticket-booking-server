package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.entity.ShowtimeEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeRepository extends JpaRepository<ShowtimeEntity, Long> {

    Optional<ShowtimeEntity> findByAuditoriumAndStartTimeAndDeletedAtIsNull(AuditoriumEntity auditorium,
            LocalDateTime startTime);

    Optional<ShowtimeEntity> findByIdAndDeletedAtIsNull(Long id);

    List<ShowtimeEntity> findAllByDeletedAtIsNull();

    List<ShowtimeEntity> findAllByMovieIdAndDeletedAtIsNull(Long movieId);

    List<ShowtimeEntity> findAllByAuditoriumIdAndDeletedAtIsNull(Long auditoriumId);

    List<ShowtimeEntity> findAllByDeletedAtAfter(LocalDateTime dateTime);
}
