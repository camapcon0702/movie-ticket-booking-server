package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowtimeResourceDto {
    private Long id;
    private Long movieId;
    private Long auditoriumId;
    private Integer basePrice;
    private LocalDateTime startTimes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
