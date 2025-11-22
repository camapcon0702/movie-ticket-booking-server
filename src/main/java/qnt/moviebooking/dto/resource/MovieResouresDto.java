package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResouresDto {
    private Long id;
    private String title;
    private String description;
    private Long durationMinutes;
    private String releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String status;
    private int starNumber;
    private String[] genres;
}
