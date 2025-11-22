package qnt.moviebooking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRequestDto {
    private String title;
    private String description;
    private Long durationMinutes;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private String releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String status;
    private int starNumber;
    private Long[] genreIds;
}
