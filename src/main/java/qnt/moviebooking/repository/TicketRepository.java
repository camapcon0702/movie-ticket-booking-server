package qnt.moviebooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.TicketEntity;
import qnt.moviebooking.enums.TicketEnums;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    boolean existsBySeatIdAndBookingShowtimeIdAndStatusIn(
            Long seatId,
            Long showtimeId,
            List<TicketEnums> statuses);

    List<TicketEntity> findByBookingIdIn(List<Long> bookingIds);
}
