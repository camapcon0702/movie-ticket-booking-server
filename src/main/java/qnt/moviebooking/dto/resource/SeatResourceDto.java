package qnt.moviebooking.dto.resource;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResourceDto {
    private Long id;
    private String rowChart;
    private String seatNumber;
    private SeatPriceResourceDto seatType;
    private boolean status;
    private Long auditoriumId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
