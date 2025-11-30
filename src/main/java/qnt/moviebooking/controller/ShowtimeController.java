package qnt.moviebooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.ShowtimeRequestDto;
import qnt.moviebooking.dto.resource.ShowtimeResourceDto;
import qnt.moviebooking.service.ShowtimeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ShowtimeController {

    private  final ShowtimeService showtimeService;

    /* ===================== ADMIN ===================== */

    @PostMapping("/admin/showtimes")
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> createShowtime (@RequestBody ShowtimeRequestDto request)
    {
        try {
           List<ShowtimeResourceDto> createdShowtime = showtimeService.createShowtimes(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo showtime thành công", createdShowtime));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi tạo showtime"+e.getMessage(), null));
        }
    }


    @PutMapping("/admin/showtimes/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResourceDto>> updateShowtime(@PathVariable Long id,
                                                                     @RequestBody ShowtimeRequestDto request) {
        try {
            ShowtimeResourceDto updateShowtime = showtimeService.updateShowtime(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật showtime thành công", updateShowtime));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi cập nhật showtime"+e.getMessage(), null));
        }
    }
    @DeleteMapping("/admin/showtimes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.softDeleteShowtime(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa showtime thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi xóa showtime"+e.getMessage(), null));
        }
    }

    @PostMapping("/admin/showtimes/rollback")
    public ResponseEntity<ApiResponse<Void>> rollbackShowtime()
    {
        try {
            showtimeService.rollBackDeletedShowtimes();
            return ResponseEntity.ok(new ApiResponse<>(true, "Rollback showtime thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi Rollback showtime"+e.getMessage(), null));
        }
    }


    /* ===================== USER ===================== */
    @GetMapping("/showtimes/{id}")
    public ResponseEntity<ApiResponse<ShowtimeResourceDto>> getDetailMovie(@PathVariable Long id) {
        try {
            ShowtimeResourceDto showtime = showtimeService.selectShowtimeDetail(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin showtime thành công", showtime));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy thông tin showtime"+e.getMessage(), null));
        }
    }

    @GetMapping("/showtimes/movie/{idmovie}")
    public ResponseEntity<ApiResponse<List<ShowtimeResourceDto>>> getAllShowtimeByMovie(@PathVariable Long idmovie) {
        try {
            //Các thông tin khac
           List<ShowtimeResourceDto> showtimes = showtimeService.selectShowtimeByMovie(idmovie);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách showtime thành công", showtimes));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách showtime"+e.getMessage(), null));
        }
    }






}
