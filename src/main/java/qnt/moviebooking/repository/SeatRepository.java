package qnt.moviebooking.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.dto.resource.SeatAvailabilityDto;
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

    List<SeatEntity> findByAuditoriumIdAndDeletedAtIsNull(Long auditoriumId);

    @Query("""
    SELECT new qnt.moviebooking.dto.resource.SeatAvailabilityDto(
        s.id,
        s.seatNumber,
        s.rowChart,
        s.seatPrice.seatType,
        CASE WHEN t.id IS NOT NULL AND ( b.status = 'SUCCESS' OR b.status = 'PENDING' ) THEN true ELSE false END,
        s.seatPrice.price
    )
    FROM SeatEntity s
    LEFT JOIN TicketEntity t ON t.seat.id = s.id
    LEFT JOIN BookingEntity b ON b.id = t.booking.id
        AND b.showtime.id = :showtimeId
    WHERE s.auditorium.id = (SELECT st.auditorium.id FROM ShowtimeEntity st WHERE st.id = :showtimeId)
    AND s.deletedAt IS NULL
    ORDER BY s.rowChart, s.seatNumber
    """)
    List<SeatAvailabilityDto> findAvailableSeatsForShowtime(@Param("showtimeId") Long showtimeId);
}

