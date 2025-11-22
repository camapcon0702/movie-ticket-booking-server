package qnt.moviebooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import qnt.moviebooking.entity.MovieEntity;
import qnt.moviebooking.enums.MovieEnums;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    List<MovieEntity> findAllByDeletedAtIsNull();

    List<MovieEntity> findDistinctByGenresIdAndDeletedAtIsNull(Long genreId);

    Optional<MovieEntity> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByTitleAndDeletedAtIsNull(String title);

    Optional<MovieEntity> findByTitleAndDeletedAtIsNull(String title);

    List<MovieEntity> findByTitleContainingIgnoreCaseAndDeletedAtIsNull(String keyword);

    List<MovieEntity> findAllByDeletedAtAfter(LocalDateTime time);

    List<MovieEntity> findAllByStatusAndDeletedAtIsNull(MovieEnums status);

    List<MovieEntity> findAllByStatusInAndDeletedAtIsNull(List<MovieEnums> statuses);

    List<MovieEntity> findByTitleContainingIgnoreCaseAndDeletedAtIsNullAndStatusIn(String keyword,
            List<MovieEnums> statuses);
}
