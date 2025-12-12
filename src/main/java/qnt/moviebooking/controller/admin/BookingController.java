package qnt.moviebooking.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.Booking.BookingUpdateRequestDto;
import qnt.moviebooking.dto.resource.Booking.BookingResourceDto;
import qnt.moviebooking.service.BookingService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/bookings")
public class BookingController {
    private final BookingService bookingService;
    @PutMapping("/{id}")
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

    @PostMapping("/rollback-deleted")
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

    @DeleteMapping("/{id}")
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

}
