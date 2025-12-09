package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.BookingServiceEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingServiceRepository  extends JpaRepository<BookingServiceEntity,Long> {

    List<BookingServiceEntity> findAllByBookingIdAndDeletedAtIsNull(Long bookingId);

    Optional<BookingServiceEntity> findByIdAndDeletedAtIsNull(Long bookingId);
}
