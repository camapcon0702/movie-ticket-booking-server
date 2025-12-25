package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FoodResourceDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}