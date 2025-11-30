package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.ShowtimeEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeRepository extends JpaRepository<ShowtimeEntity,Long> {

    List <ShowtimeEntity> findByAuditoriumIdAndStartTimeIn(Long id, List<LocalDateTime> startTime);

    List <ShowtimeEntity> findByMovieId(Long movieId);

    Optional<ShowtimeEntity> findByIdAndDeletedAtIsNull(Long id);

    List <ShowtimeEntity> findByMovieIdAndDeletedAtIsNotNull(Long movieId);
}
