package qnt.moviebooking.controller.client;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.Booking.BookingRequestDto;
import qnt.moviebooking.dto.resource.Booking.BookingDetailResourceDto;
import qnt.moviebooking.dto.resource.Booking.BookingResourceDto;
import qnt.moviebooking.service.BookingService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingControler {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResourceDto>> createBooking(@RequestBody BookingRequestDto request)
    {
        try {
            BookingResourceDto createBooking =  bookingService.createBooking(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo booking thành công", createBooking));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi tạo booking", null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResourceDto>>> getAllBookings() {
        try {
            List<BookingResourceDto> bookings =  bookingService.getAllBookings();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách booking thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách bookings", null));
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<List<BookingResourceDto>>> getAllBookingsForUser(@PathVariable Long id) {
        try {
            List<BookingResourceDto> bookings =  bookingService.getAllBookingsByUserId(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách booking thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách bookings", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDetailResourceDto>> getDetailBookingsForUser(@PathVariable Long id) {
        try {
            BookingDetailResourceDto booking =  bookingService.getBookingDetail(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy booking thành công", booking));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy booking", null));
        }
    }

}
