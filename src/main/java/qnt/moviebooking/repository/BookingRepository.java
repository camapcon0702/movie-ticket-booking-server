package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.enums.BookingEnums;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByUserId(Long userId);

    List<BookingEntity> findAll();

    Optional<BookingEntity> findById(Long bookingId);

    Optional<BookingEntity> findByIdAndStatus(Long id, BookingEnums status);

}
