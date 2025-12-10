package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.MovieRequestDto;
import qnt.moviebooking.dto.resource.MovieResouresDto;
import qnt.moviebooking.service.MovieService;

@RestController("AdminMovieController")
@RequiredArgsConstructor
@RequestMapping("/admin/movies")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResouresDto>> createMovie(@RequestBody MovieRequestDto request) {

        MovieResouresDto createdMovie = movieService.createMovie(request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo phim thành công", createdMovie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResouresDto>> updateMovie(@PathVariable Long id,
            @RequestBody MovieRequestDto request) {

        MovieResouresDto updatedMovie = movieService.updateMovie(id, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật phim thành công", updatedMovie));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedMovies() {

        movieService.rollBackDeletedMovies();

        return ResponseEntity.ok(new ApiResponse<>(true, "Khôi phục phim đã xóa thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getAllMoviesAdmin() {

        List<MovieResouresDto> movies = movieService.getAllMoviesAdmin();

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim thành công.", movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResouresDto>> getMovieByIdAdmin(@PathVariable Long id) {

        MovieResouresDto movie = movieService.getMovieByIdAdmin(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phim thành công", movie));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> searchMoviesByTitleAdmin(@RequestParam String keyword) {

        List<MovieResouresDto> movies = movieService.searchMoviesByTitleAdmin(keyword);

        return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm phim thành công.", movies));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {

        movieService.deleteMovie(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa phim thành công.", null));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getMoviesByGenreAdmin(@PathVariable Long genreId) {

        List<MovieResouresDto> movies = movieService.getMoviesByGenreId(genreId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim theo thể loại thành công.", movies));
    }
}