package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenreResourceDto {
    private Long id;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;
}
