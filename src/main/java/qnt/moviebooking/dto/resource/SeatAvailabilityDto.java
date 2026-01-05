package qnt.moviebooking.dto.resource;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityDto {
    private Long id;
    private String seatNumber;
    private String rowName;
    private String seatType;
    private Boolean isBooked;
    private BigDecimal price;


    public Boolean isAvailable() {
        return !isBooked;
    }
}
