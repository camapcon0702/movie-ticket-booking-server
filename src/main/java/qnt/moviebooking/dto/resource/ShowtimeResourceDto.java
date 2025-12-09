package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowtimeResourceDto {
    private Long  id;
    private Long movieId;
    private Long auditoriumId;
    private BigDecimal basePrice;
    private LocalDateTime startTimes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
