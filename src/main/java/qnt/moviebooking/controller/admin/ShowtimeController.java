package qnt.moviebooking.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.ShowtimeRequestDto;
import qnt.moviebooking.dto.resource.ShowtimeResourceDto;
import qnt.moviebooking.service.ShowtimeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> createShowtime(
            @RequestBody ShowtimeRequestDto request) {

        List<ShowtimeResourceDto> createdShowtime = showtimeService.createShowtimes(request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo showtime thành công", createdShowtime));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResourceDto>> updateShowtime(@PathVariable Long id,
            @RequestBody ShowtimeRequestDto request) {

        ShowtimeResourceDto updateShowtime = showtimeService.updateShowtime(id, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật showtime thành công", updateShowtime));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowtime(@PathVariable Long id) {

        showtimeService.softDeleteShowtime(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa showtime thành công", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollbackShowtime() {

        showtimeService.rollBackDeletedShowtimes();

        return ResponseEntity.ok(new ApiResponse<>(true, "Rollback showtime thành công", null));
    }
}