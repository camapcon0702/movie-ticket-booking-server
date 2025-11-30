package qnt.moviebooking.dto.request.Showtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowtimeRequestDto {
    private Long movieId;
    private Long auditoriumId;
    private Integer basePrice;
    private List<LocalDateTime> startTimes;
}
