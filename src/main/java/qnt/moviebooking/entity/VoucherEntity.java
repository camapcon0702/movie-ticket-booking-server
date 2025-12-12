package qnt.moviebooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_Voucher")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private  Long id;
    private  String code;
    private BigDecimal discount_amount;
    private  Double discount_percentage;
    private LocalDateTime expiry_date;
=======
    private Long id;
    @Column(unique = true)
    private String code;
    private Double discountAmount;
    private Double discountPercentage;
    private Double discountMax;
    private LocalDateTime expiryDate;
    private boolean active;
>>>>>>> 2c10bc1e2b7f2469448d9beaf8f3dac5aa3aa5f5

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
