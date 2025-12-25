package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.BookingFoodEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingServiceRepository  extends JpaRepository<BookingFoodEntity,Long> {

    List<BookingFoodEntity> findAllByBookingIdAndDeletedAtIsNull(Long bookingId);

    Optional<BookingFoodEntity> findByIdAndDeletedAtIsNull(Long bookingId);
}
