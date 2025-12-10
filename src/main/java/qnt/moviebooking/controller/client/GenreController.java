package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.GenreResourceDto;
import qnt.moviebooking.service.GenreService;

@RestController("genreClientController")
@RequestMapping("/v1.0/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResourceDto>>> getAllGenreForUser() {

        List<GenreResourceDto> genres = genreService.getAllGenres();

        return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thể loại phim thành công", genres));
    }
}
