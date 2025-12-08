package qnt.moviebooking.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatsRequestDto {
    private Long auditoriumId;
    private String rowChart;
    private List<String> seatNumbers;
    private String seatType;
}
