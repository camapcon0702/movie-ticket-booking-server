package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.SeatResourceDto;
import qnt.moviebooking.service.SeatService;

@RestController("ClientSeatController")
@RequestMapping("v1.0/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/auditorium/{id}")
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> getSeatsByAuditorium(@PathVariable Long id) {

        List<SeatResourceDto> seats = seatService.getSeatsByAuditorium(id);

        return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thông tin ghế thành công", seats));
    }

    @GetMapping("/auditorium/{id}/filter")
    public ResponseEntity<ApiResponse<List<SeatResourceDto>>> getSeatsByIdsAndAuditorium(@PathVariable Long id,
            @RequestBody List<Long> seatsId) {
        List<SeatResourceDto> seats = seatService.getSeatsByIdsAndAuditorium(id, seatsId);

        return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thông tin ghế thành công", seats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatResourceDto>> getSeatById(@PathVariable Long id) {

        SeatResourceDto seat = seatService.getSeatById(id);

        return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thông tin ghế thành công", seat));
    }
}
