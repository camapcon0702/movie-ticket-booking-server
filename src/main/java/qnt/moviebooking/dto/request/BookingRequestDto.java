package qnt.moviebooking.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequestDto {
    private Long showtimeId;
    private Long voucherId;
    private Long[] seatId;
    private List<OrderFoodRequestDto> orders;
}
