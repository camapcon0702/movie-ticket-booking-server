package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.MovieRequestDto;
import qnt.moviebooking.dto.resource.MovieResourceDto;
import qnt.moviebooking.service.MovieService;

@RestController("AdminMovieController")
@RequiredArgsConstructor
@RequestMapping("/admin/movies")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResourceDto>> createMovie(@RequestBody MovieRequestDto request) {

        MovieResourceDto createdMovie = movieService.createMovie(request);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo phim thành công", createdMovie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResourceDto>> updateMovie(@PathVariable Long id,
            @RequestBody MovieRequestDto request) {

        MovieResourceDto updatedMovie = movieService.updateMovie(id, request);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật phim thành công", updatedMovie));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedMovies() {

        movieService.rollBackDeletedMovies();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Khôi phục phim đã xóa thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResourceDto>>> getAllMoviesAdmin() {

        List<MovieResourceDto> movies = movieService.getAllMoviesAdmin();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách phim thành công.", movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResourceDto>> getMovieByIdAdmin(@PathVariable Long id) {

        MovieResourceDto movie = movieService.getMovieByIdAdmin(id);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin phim thành công", movie));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieResourceDto>>> searchMoviesByTitleAdmin(@RequestParam String keyword) {

        List<MovieResourceDto> movies = movieService.searchMoviesByTitleAdmin(keyword);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Tìm kiếm phim thành công.", movies));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {

        movieService.deleteMovie(id);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xóa phim thành công.", null));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<MovieResourceDto>>> getMoviesByGenreAdmin(@PathVariable Long genreId) {

        List<MovieResourceDto> movies = movieService.getMoviesByGenreId(genreId);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy danh sách phim theo thể loại thành công.", movies));
    }
}