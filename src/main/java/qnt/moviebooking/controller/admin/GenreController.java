package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
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

@RestController("AdminGenreController")
@RequiredArgsConstructor
@RequestMapping("/admin/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<ApiResponse<GenreResourceDto>> createGenre(@RequestBody GenreRequestDto request) {

        GenreResourceDto createdGenre = genreService.createGenre(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo thể loại phim thành công", createdGenre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResourceDto>> updateGenre(@PathVariable Long id,
            @RequestBody GenreRequestDto request) {

        GenreResourceDto updatedGenre = genreService.updateGenre(id, request);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật thể loại phim thành công", updatedGenre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GenreResourceDto>> getGenreById(@PathVariable Long id) {

        GenreResourceDto genre = genreService.getGenreById(id);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thể loại phim thành công", genre));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GenreResourceDto>>> getAllGenres() {

        List<GenreResourceDto> genres = genreService.getAllGenres();

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách thể loại phim thành công", genres));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<GenreResourceDto>> getGenreByName(@PathVariable String name) {

        GenreResourceDto genre = genreService.getGenreByName(name);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thể loại phim thành công", genre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGenre(@PathVariable Long id) {

        genreService.deleteGenre(id);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Xóa thể loại phim thành công", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedGenres() {

        genreService.rollBackDeletedGenres();

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Khôi phục thể loại phim đã xóa thành công", null));
    }
}