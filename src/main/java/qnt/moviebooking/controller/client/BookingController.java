package qnt.moviebooking.controller.client;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.BookingRequestDto;
import qnt.moviebooking.dto.resource.BookingResourceDto;
import qnt.moviebooking.service.BookingService;

import java.util.List;

@RestController("ClientBookingController")
@RequiredArgsConstructor
@RequestMapping("/v1.0/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResourceDto>> createBooking(@RequestBody BookingRequestDto request) {

        BookingResourceDto response = bookingService.createBooking(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo Booking thành công", response));
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<BookingResourceDto>> getBooking(@PathVariable Long id) {
        BookingResourceDto response = bookingService.getBookingById(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.OK.value(), "Lấy booking thành công", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<BookingResourceDto>>> getCurrentUserBooking() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách booking của user thành công", bookingService.getBookingsByUser()));
    }
}