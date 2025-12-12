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

<<<<<<< HEAD
@RestController("genreClientController")
=======
@RestController("ClientGenreController")
>>>>>>> 2c10bc1e2b7f2469448d9beaf8f3dac5aa3aa5f5
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
