package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.MovieResourceDto;
import qnt.moviebooking.enums.MovieEnums;
import qnt.moviebooking.repository.MovieRepository;
import qnt.moviebooking.service.MovieService;

@RestController("ClientMovieController")
@RequestMapping("/v1.0/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieRepository movieRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResourceDto>>> getAllMoviesForUser() {

        List<MovieResourceDto> movies = movieService.getAllMoviesForUser();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách phim thành công.", movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResourceDto>> getMovieByIdForUser(@PathVariable Long id) {

        MovieResourceDto movie = movieService.getMovieByIdForUser(id);

        if (movie == null) {
            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK.value(), "Phim không khả dụng cho người dùng!", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin phim thành công.", movie));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieResourceDto>>> searchMoviesByTitleForUser(
            @RequestParam String keyword) {

        List<MovieResourceDto> movies = movieService.searchMoviesByTitleForUser(keyword);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Tìm kiếm phim thành công.", movies));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<ApiResponse<List<MovieResourceDto>>> getMoviesByGenreForUser(@PathVariable Long genreId) {

        List<MovieResourceDto> movies = movieService.getMoviesByGenreForUser(genreId);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy danh sách phim theo thể loại thành công.", movies));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<MovieResourceDto>>> getMoviesByStatusForUser(@RequestParam MovieEnums status) {

        List<MovieResourceDto> movies = movieService.getMoviesByStatusForUser(status);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách phim thành công.", movies));
    }
}
