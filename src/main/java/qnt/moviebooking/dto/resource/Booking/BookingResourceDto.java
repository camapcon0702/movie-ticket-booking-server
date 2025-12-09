package qnt.moviebooking.dto.resource.Booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qnt.moviebooking.enums.BookingEnums;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResourceDto {
    private Long id;
    private BigDecimal total;
    private String nameMovie;
    private LocalDateTime showtime;
    private BookingEnums status;
    private LocalDate startTimes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
