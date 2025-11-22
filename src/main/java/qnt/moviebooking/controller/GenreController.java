package qnt.moviebooking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.GenreRequestDto;
import qnt.moviebooking.dto.resource.GenreResourceDto;
import qnt.moviebooking.service.GenreService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/genres")
public class GenreController {
    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<ApiResponse<GenreResourceDto>> createGenre(@RequestBody GenreRequestDto request) {
        try {
            GenreResourceDto createdGenre = genreService.createGenre(request);

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Tạo thể loại phim thành công", createdGenre));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi tạo thể loại phim", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResourceDto>> updateGenre(@PathVariable Long id,
            @RequestBody GenreRequestDto request) {
        try {
            GenreResourceDto updatedGenre = genreService.updateGenre(id, request);

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Cập nhật thể loại phim thành công", updatedGenre));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi cập nhật thể loại phim", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResourceDto>> getGenreById(@PathVariable Long id) {
        try {
            GenreResourceDto genre = genreService.getGenreById(id);

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thể loại phim thành công", genre));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy thể loại phim", null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResourceDto>>> getAllGenres() {

        try {
            List<GenreResourceDto> genres = genreService.getAllGenres();

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy danh sách thể loại phim thành công", genres));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách thể loại phim", null));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<GenreResourceDto>> getGenreByName(@PathVariable String name) {
        try {
            GenreResourceDto genre = genreService.getGenreByName(name);

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thể loại phim thành công", genre));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy thể loại phim", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable Long id) {
        try {
            genreService.deleteGenre(id);

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Xóa thể loại phim thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi xóa thể loại phim", null));
        }
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedGenres() {
        try {
            genreService.rollBackDeletedGenres();

            return ResponseEntity.ok().body(new ApiResponse<>(true, "Khôi phục thể loại phim đã xóa thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi khôi phục thể loại phim đã xóa", null));
        }
    }
}
