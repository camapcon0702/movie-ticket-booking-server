package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.enums.BookingEnums;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByUserId(Long userId);

    List<BookingEntity> findAll();

    List<BookingEntity> findByStatusAndCreatedAtBefore(BookingEnums status, LocalDateTime createdAt);

    Optional<BookingEntity> findById(Long bookingId);

    Optional<BookingEntity> findByIdAndStatus(Long id, BookingEnums status);

}
