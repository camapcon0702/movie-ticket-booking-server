package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowtimeResourceDto {
    private Long movieId;
    private Long auditoriumId;
    private Integer basePrice;
    private LocalTime[] startTimes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
