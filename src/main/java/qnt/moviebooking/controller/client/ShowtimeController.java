package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.ShowtimeResourceDto;
import qnt.moviebooking.service.ShowtimeService;

@RestController("ClientShowtimeController")
@RequestMapping("/v1.0/showtime")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResourceDto>> getShowtimeById(@PathVariable Long id) {

        ShowtimeResourceDto showtime = showtimeService.getShowtimeById(id);

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin showtime thành công", showtime));
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> getAllShowtimeByMovie(@PathVariable Long id) {
        List<ShowtimeResourceDto> showtime = showtimeService.getShowtimeByMovie(id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy danh sách showtime thành công", showtime));
    }
}
