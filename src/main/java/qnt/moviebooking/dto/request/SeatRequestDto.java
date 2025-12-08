package qnt.moviebooking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatRequestDto {
    private Long auditoriumId;
    private String rowChart;
    private String seatNumber;
    private String seatType;
}
