package qnt.moviebooking.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.enums.BookingEnums;
import qnt.moviebooking.repository.BookingRepository;
import qnt.moviebooking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Scheduled(fixedRate = 60_000) // 1 minute
    public void cancelExpiredBookings() {
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(15);

        List<BookingEntity> expiredBookings =
                bookingRepository.findByStatusAndCreatedAtBefore(BookingEnums.PENDING, expiredTime);

        if (expiredBookings.isEmpty()) {
            return;
        }
        expiredBookings.forEach(booking -> {
            try {
                bookingService.cancel(booking);
            } catch (Exception ex) {
                log.error("Failed to cancel booking {}", booking.getId(), ex);
            }
        });
    }
}
