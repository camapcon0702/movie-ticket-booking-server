package qnt.moviebooking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.TicketEntity;

import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<TicketEntity,Long> {

    @Query("SELECT t FROM TicketEntity t " +
            "WHERE t.booking.showtime.id = :showtimeId " +
            "AND t.deletedAt IS NULL " +
            "AND t.booking.deletedAt IS NULL")
    List<TicketEntity> findBookedSeatsByShowtime( Long showtimeId);

    List<TicketEntity> findAllByBookingIdAndDeletedAtIsNull(Long bookingId);
}
