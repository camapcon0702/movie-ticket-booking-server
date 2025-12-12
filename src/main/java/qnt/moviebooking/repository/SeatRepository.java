package qnt.moviebooking.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.SeatEntity;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findAllByDeletedAtIsNull();

    List<SeatEntity> findAllByAuditoriumIdAndDeletedAtIsNull(Long auditoriumId);

    Optional<SeatEntity> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByAuditoriumIdAndRowChartAndSeatNumberAndDeletedAtIsNull(Long auditoriumId, String rowChart,
            String seatNumber);

    List<SeatEntity> findAllByAuditoriumIdAndRowChartAndSeatNumberInAndDeletedAtIsNull(
            Long auditoriumId, String rowChart, List<String> seatNumbers);

    List<SeatEntity> findAllByAuditoriumIdAndIdInAndDeletedAtIsNull(Long auditoriumId, List<Long> seatIds);

    List<SeatEntity> findAllByDeletedAtAfter(LocalDateTime time);
}

