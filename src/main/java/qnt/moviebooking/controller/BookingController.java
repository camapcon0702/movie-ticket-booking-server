package qnt.moviebooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.Booking.BookingRequestDto;
import qnt.moviebooking.dto.request.Booking.BookingUpdateRequestDto;
import qnt.moviebooking.dto.resource.Booking.BookingDetailResourceDto;
import qnt.moviebooking.dto.resource.Booking.BookingResourceDto;
import qnt.moviebooking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BookingController {

    private final BookingService bookingService;

    /* ===================== ADMIN ===================== */

    @PutMapping("/admin/bookings/{id}")
    public ResponseEntity<ApiResponse<BookingResourceDto>> updateBooking(@PathVariable Long id,
                                                                     @RequestBody BookingUpdateRequestDto request) {
        try {
           BookingResourceDto  updatedBooking = bookingService.updateBooking(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật Booking thành công", updatedBooking));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi cập nhật Booking", null));
        }
    }



    @PostMapping("/admin/bookings/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedBookings() {
        try {
            bookingService.rollbackDeleteBooking();
            return ResponseEntity.ok(new ApiResponse<>(true, "Khôi phục booking đã xóa thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi khôi phục booking đã xóa", null));
        }
    }

    @DeleteMapping("/admin/bookings/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.softDeleteBooking(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa booking thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi xóa booking", null));
        }
    }


    /* ===================== USER ===================== */

    @PostMapping("/bookings")
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

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResourceDto>>> getAllBookings() {
        try {
            List<BookingResourceDto> bookings =  bookingService.getAllBookings();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách booking thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách bookings", null));
        }
    }

    @GetMapping("/bookings/user/{id}")
    public ResponseEntity<ApiResponse<List<BookingResourceDto>>> getAllBookingsForUser(@PathVariable Long id) {
        try {
            List<BookingResourceDto> bookings =  bookingService.getAllBookingsByUserId(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách booking thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách bookings", null));
        }
    }

    @GetMapping("/bookings/{id}")
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
