package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeSeatResourceDto {
    private Long id;
    private String rowChart;
    private String seatNumber;
    private SeatPriceResourceDto seatType;
    private boolean isBooked;
    private boolean status;
    private Long auditoriumId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
