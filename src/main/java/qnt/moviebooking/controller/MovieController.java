package qnt.moviebooking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.MovieRequestDto;
import qnt.moviebooking.dto.resource.MovieResouresDto;
import qnt.moviebooking.service.MovieService;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class MovieController {

    private final MovieService movieService;

    /* ===================== ADMIN ===================== */

    @PostMapping("/admin/movies")
    public ResponseEntity<ApiResponse<MovieResouresDto>> createMovie(@RequestBody MovieRequestDto request) {
        try {
            MovieResouresDto createdMovie = movieService.createMovie(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo phim thành công", createdMovie));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi tạo phim", null));
        }
    }

    @PutMapping("/admin/movies/{id}")
    public ResponseEntity<ApiResponse<MovieResouresDto>> updateMovie(@PathVariable Long id,
            @RequestBody MovieRequestDto request) {
        try {
            MovieResouresDto updatedMovie = movieService.updateMovie(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật phim thành công", updatedMovie));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi cập nhật phim", null));
        }
    }

    @PostMapping("/admin/movies/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedMovies() {
        try {
            movieService.rollBackDeletedMovies();
            return ResponseEntity.ok(new ApiResponse<>(true, "Khôi phục phim đã xóa thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi khôi phục phim đã xóa", null));
        }
    }

    @GetMapping("/admin/movies/{id}")
    public ResponseEntity<ApiResponse<MovieResouresDto>> getMovieByIdAdmin(@PathVariable Long id) {
        try {
            MovieResouresDto movie = movieService.getMovieByIdAdmin(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phim thành công", movie));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy thông tin phim", null));
        }
    }

    @GetMapping("/admin/movies")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getAllMoviesAdmin() {
        try {
            List<MovieResouresDto> movies = movieService.getAllMoviesAdmin();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim thành công", movies));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách phim", null));
        }
    }

    @GetMapping("/admin/movies/search")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> searchMoviesByTitleAdmin(@RequestParam String keyword) {
        try {
            List<MovieResouresDto> movies = movieService.searchMoviesByTitleAdmin(keyword);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm phim thành công", movies));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi tìm kiếm phim", null));
        }
    }

    @DeleteMapping("/admin/movies/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa phim thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi xóa phim", null));
        }
    }

    @GetMapping("/admin/movies/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getMoviesByGenreAdmin(@PathVariable Long genreId) {
        try {
            List<MovieResouresDto> movies = movieService.getMoviesByGenreId(genreId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim theo thể loại thành công", movies));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách phim theo thể loại", null));
        }
    }

    /* ===================== USER ===================== */

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getAllMoviesForUser() {
        try {
            List<MovieResouresDto> movies = movieService.getAllMoviesForUser();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim thành công", movies));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách phim", null));
        }
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<ApiResponse<MovieResouresDto>> getMovieByIdForUser(@PathVariable Long id) {
        try {
            MovieResouresDto movie = movieService.getMovieByIdForUser(id);
            if (movie == null) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Phim không khả dụng cho người dùng", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phim thành công", movie));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy thông tin phim", null));
        }
    }

    @GetMapping("/movies/search")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> searchMoviesByTitleForUser(
            @RequestParam String keyword) {
        try {
            List<MovieResouresDto> movies = movieService.searchMoviesByTitleForUser(keyword);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm phim thành công", movies));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi tìm kiếm phim", null));
        }
    }

    @GetMapping("/movies/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<MovieResouresDto>>> getMoviesByGenreForUser(@PathVariable Long genreId) {
        try {
            List<MovieResouresDto> movies = movieService.getMoviesByGenreForUser(genreId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phim theo thể loại thành công", movies));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách phim theo thể loại", null));
        }
    }

}
