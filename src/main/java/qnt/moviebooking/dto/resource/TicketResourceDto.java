package qnt.moviebooking.dto.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketResourceDto {
    Long id;
    private BigDecimal price;
    private String seat_name;
    private String audi_name;
}
