package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.SeatRequestDto;
import qnt.moviebooking.dto.request.SeatsRequestDto;
import qnt.moviebooking.dto.resource.SeatResourceDto;
import qnt.moviebooking.service.SeatService;

@RestController("AdminSeatController")
@RequiredArgsConstructor
@RequestMapping("/admin/seats")
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> createSeats(@RequestBody SeatsRequestDto request) {
        List<SeatResourceDto> seats = seatService.createSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo ghế thành công!", seats));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResourceDto>> updateSeat(@PathVariable Long id,
            @RequestBody SeatRequestDto request) {
        SeatResourceDto seat = seatService.updateSeat(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật ghế thành công!", seat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xoá ghế thành công!", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollbackDeletedSeat() {
        seatService.rollbackDeletedSeat();
        return ResponseEntity.ok(new ApiResponse<>(true, "Khôi phục các ghế đã xóa thành công!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResourceDto>> getSeatById(@PathVariable Long id) {
        SeatResourceDto seat = seatService.getSeatById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin ghế thành công!", seat));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> getAllSeat() {
        List<SeatResourceDto> seats = seatService.getAllSeats();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin ghế thành công!", seats));
    }

    @GetMapping("/auditorium/{id}")
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> getSeatsByAuditorium(@PathVariable Long id) {
        List<SeatResourceDto> seats = seatService.getSeatsByAuditorium(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin ghế thành công!", seats));
    }
}