package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qnt.moviebooking.enums.BookingEnums;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResourceDto {
    private Long id;
    private BigDecimal total;
    private String nameMovie;
    private LocalDateTime startTime;
    private BookingEnums status;
    private List<TicketResourceDto> tickets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
