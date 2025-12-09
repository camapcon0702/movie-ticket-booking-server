package qnt.moviebooking.dto.request.Booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingUpdateRequestDto {
    private Long voucher_id;
    private Long[] serviceids;
    private Long[] seatids;
}
