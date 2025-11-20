package qnt.moviebooking.dto.resource;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResourceDto {
    private Long id;
    private String email;
    private String fullName;
    private String roleName;
    private LocalDateTime createAt;
}