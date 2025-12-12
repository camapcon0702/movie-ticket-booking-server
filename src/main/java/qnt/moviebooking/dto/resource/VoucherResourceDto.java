package qnt.moviebooking.dto.resource;

<<<<<<< HEAD
=======
import java.time.LocalDateTime;

>>>>>>> 2c10bc1e2b7f2469448d9beaf8f3dac5aa3aa5f5
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
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
=======
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
>>>>>>> 2c10bc1e2b7f2469448d9beaf8f3dac5aa3aa5f5
}
