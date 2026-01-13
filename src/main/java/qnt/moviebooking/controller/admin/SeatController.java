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
@RequestMapping("/v1.0/admin/seats")
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> createSeats(@RequestBody SeatsRequestDto request) {
        List<SeatResourceDto> seats = seatService.createSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo ghế thành công!", seats));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResourceDto>> updateSeat(@PathVariable Long id,
            @RequestBody SeatRequestDto request) {
        SeatResourceDto seat = seatService.updateSeat(id, request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật ghế thành công!", seat));
    }

    @PutMapping("{id}/type")
    public ResponseEntity<ApiResponse<SeatResourceDto>> updateSeatType(@PathVariable Long id, @RequestParam Long typeId) {
        SeatResourceDto seat = seatService.updateSeatType(id, typeId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật loại ghế thành công!", seat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xoá ghế thành công!", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollbackDeletedSeat() {
        seatService.rollbackDeletedSeat();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Khôi phục các ghế đã xóa thành công!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResourceDto>> getSeatById(@PathVariable Long id) {
        SeatResourceDto seat = seatService.getSeatById(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin ghế thành công!", seat));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> getAllSeat() {
        List<SeatResourceDto> seats = seatService.getAllSeats();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin ghế thành công!", seats));
    }

    @GetMapping("/auditorium/{id}")
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> getSeatsByAuditorium(@PathVariable Long id) {
        List<SeatResourceDto> seats = seatService.getSeatsByAuditorium(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin ghế thành công!", seats));
    }
}