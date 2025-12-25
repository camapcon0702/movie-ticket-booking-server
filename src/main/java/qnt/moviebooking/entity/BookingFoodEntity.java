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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Table(name = "tbl_booking_foods")
    public class BookingFoodEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        private Integer quantity;
        private BigDecimal price;
        @Column(updatable = false)
        @CreationTimestamp
        private LocalDateTime createdAt;
        @UpdateTimestamp
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "booking_id", nullable = false)
        private BookingEntity booking;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "food_id", nullable = false)
        private FoodEntity food;
    }
