package qnt.moviebooking.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeBulkRequestDto {
    private Long movieId;
    private Long auditoriumId;
    private List<LocalDateTime> startTimes;
    private BigDecimal basePrice;
}
