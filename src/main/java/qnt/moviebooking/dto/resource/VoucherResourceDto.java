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
public class VoucherResourceDto {
    private Long id;
    private String code;
    private Double discountAmount;
    private Double discountPercentage;
    private Double discountMax;
    private LocalDateTime expiryDate;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
