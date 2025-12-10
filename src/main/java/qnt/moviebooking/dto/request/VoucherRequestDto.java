package qnt.moviebooking.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherRequestDto {
    private String code;
    private Double discountAmount;
    private Double discountPercentage;
    private Double discountMax;
    private LocalDateTime expiryDate;
    private boolean active;
}
