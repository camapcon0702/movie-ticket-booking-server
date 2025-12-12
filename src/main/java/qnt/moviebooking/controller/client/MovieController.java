package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.MovieResouresDto;
import qnt.moviebooking.service.MovieService;

<<<<<<< HEAD
@RestController("movieClientController")
=======
@RestController("ClientMovieController")
>>>>>>> 2c10bc1e2b7f2469448d9beaf8f3dac5aa3aa5f5
@RequestMapping("/v1.0/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getAllMoviesForUser() {

        List<MovieResouresDto> movies = movieService.getAllMoviesForUser();

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim thành công.", movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResouresDto>> getMovieByIdForUser(@PathVariable Long id) {

        MovieResouresDto movie = movieService.getMovieByIdForUser(id);

        if (movie == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Phim không khả dụng cho người dùng!", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phim thành công.", movie));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> searchMoviesByTitleForUser(
            @RequestParam String keyword) {

        List<MovieResouresDto> movies = movieService.searchMoviesByTitleForUser(keyword);

        return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm phim thành công.", movies));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getMoviesByGenreForUser(@PathVariable Long genreId) {

        List<MovieResouresDto> movies = movieService.getMoviesByGenreForUser(genreId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim theo thể loại thành công.", movies));
    }
}
