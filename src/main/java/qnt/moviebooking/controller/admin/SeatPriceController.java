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
import qnt.moviebooking.dto.request.SeatPriceRequestDto;
import qnt.moviebooking.dto.resource.SeatPriceResourceDto;
import qnt.moviebooking.service.SeatPriceService;

@RestController("AdminSeatPriceController")
@RequestMapping("/v1.0/admin/seat-prices")
@RequiredArgsConstructor
public class SeatPriceController {
    private final SeatPriceService seatPriceService;

    @PostMapping
    public ResponseEntity<ApiResponse<SeatPriceResourceDto>> createSeatPrice(@RequestBody SeatPriceRequestDto request) {
        SeatPriceResourceDto response = seatPriceService.createSeatPrice(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo giá loại ghế thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatPriceResourceDto>> updateSeatPrice(@PathVariable Long id,
            @RequestBody SeatPriceRequestDto request) {
        SeatPriceResourceDto response = seatPriceService.updateSeatPrice(id, request);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật giá loại ghế thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeatPrice(@PathVariable Long id) {
        seatPriceService.deleteSeatPrice(id);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Xoá giá loại ghế thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatPriceResourceDto>>> getAllSeatPrices() {
        List<SeatPriceResourceDto> responses = seatPriceService.getAllSeatPrice();

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin giá loại ghế thành công", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatPriceResourceDto>> getSeatPriceById(@PathVariable Long id) {
        SeatPriceResourceDto response = seatPriceService.getSeatTypeById(id);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin giá loại ghế thành công", response));
    }
}
