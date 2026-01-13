package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.BookingResourceDto;
import qnt.moviebooking.service.BookingService;

@RestController("AdminBookingController")
@RequestMapping("/v1.0/admin/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResourceDto>>> getAllBookings() {
        List<BookingResourceDto> bookings = bookingService.getAllBookings();

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Fetched all bookings successfully", bookings));
    }
}
