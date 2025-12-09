package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherResourceDto {
    Long id;
    private  String code;
    private BigDecimal discount_amount;
    private  Double discount_percentage;
    private LocalDateTime expiry_date;
}
