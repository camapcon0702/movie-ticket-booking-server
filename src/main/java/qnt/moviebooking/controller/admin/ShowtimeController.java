package qnt.moviebooking.controller.admin;

import lombok.RequiredArgsConstructor;

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

import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.ShowtimeBulkRequestDto;
import qnt.moviebooking.dto.request.ShowtimeRequestDto;
import qnt.moviebooking.dto.resource.ShowtimeResourceDto;
import qnt.moviebooking.service.ShowtimeService;

@RestController("AdminShowtimeController")
@RequiredArgsConstructor
@RequestMapping("/v1.0/admin/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> createShowtime(
            @RequestBody ShowtimeBulkRequestDto request) {

        List<ShowtimeResourceDto> response = showtimeService.createBulkShowtime(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo showtime thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResourceDto>> updateShowtime(@PathVariable Long id,
            @RequestBody ShowtimeRequestDto request) {

        ShowtimeResourceDto response = showtimeService.updateShowtime(id, request);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật showtime thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowtime(@PathVariable Long id) {

        showtimeService.deletedShowtime(id);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xóa showtime thành công", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollbackShowtime() {

        showtimeService.rollBackDeletedShowtime();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Rollback showtime thành công", null));
    }

    @GetMapping("/auditorium/{id}")
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> getShowtimeByAuditoriumId(@PathVariable Long id) {
        List<ShowtimeResourceDto> response = showtimeService.getShowtimeByAuditorium(id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy thông tin showtime thành công", response));
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> getAllShowtimeByMovie(@PathVariable Long id) {
        List<ShowtimeResourceDto> showtime = showtimeService.getShowtimeByMovie(id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy danh sách showtime thành công", showtime));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResourceDto>> getShowtimeById(@PathVariable Long id) {

        ShowtimeResourceDto showtime = showtimeService.getShowtimeById(id);

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin showtime thành công", showtime));
    }
}