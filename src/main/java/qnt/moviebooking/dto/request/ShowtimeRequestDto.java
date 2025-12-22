package qnt.moviebooking.dto.request;

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
public class ShowtimeRequestDto {
    private Long movieId;
    private Long auditoriumId;
    private BigDecimal basePrice;
    private LocalDateTime startTime;
}
