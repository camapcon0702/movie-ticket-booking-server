package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.ShowtimeResourceDto;
import qnt.moviebooking.service.ShowtimeService;

@RestController
@RequestMapping("v1.0/")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResourceDto>> getDetailMovie(@PathVariable Long id) {

        ShowtimeResourceDto showtime = showtimeService.selectShowtimeDetail(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin showtime thành công", showtime));
    }

    @GetMapping("/movie/{idmovie}")
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> getAllShowtimeByMovie(@PathVariable Long idmovie) {

        List<ShowtimeResourceDto> showtimes = showtimeService.selectShowtimeByMovie(idmovie);

        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách showtime thành công", showtimes));
    }
}
