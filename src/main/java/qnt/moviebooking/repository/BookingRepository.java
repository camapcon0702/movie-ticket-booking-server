package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.enums.BookingEnums;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long> {

    List<BookingEntity> findAllByDeletedAtIsNull();

    List<BookingEntity> findAllByUserIdAndDeletedAtIsNull(Long id);

    List<BookingEntity> findAllByDeletedAtAfter(LocalDateTime date);

    Optional<BookingEntity> findByIdAndDeletedAtIsNull(Long bookingId);
    Optional<BookingEntity> findByIdAndDeletedAtIsNullAndStatus(Long id, BookingEnums status);

}
