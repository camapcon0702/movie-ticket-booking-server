package qnt.moviebooking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private List<LocalDateTime> startTimes;
}
