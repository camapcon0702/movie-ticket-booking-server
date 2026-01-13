package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.AuditoriumRequestDto;
import qnt.moviebooking.dto.resource.AuditoriumResourceDto;
import qnt.moviebooking.service.AuditoriumService;

@RestController("AdminAuditoriumController")
@RequiredArgsConstructor
@RequestMapping("/v1.0/admin/auditoriums")
public class AuditoriumController {

    private final AuditoriumService auditoriumService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuditoriumResourceDto>> createAuditorium(
            @RequestBody AuditoriumRequestDto requestDto) {

        AuditoriumResourceDto auditorium = auditoriumService.createAuditorium(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo phòng chiếu thành công", auditorium));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditoriumResourceDto>> updateAuditorium(@PathVariable Long id,
            @RequestBody AuditoriumRequestDto requestDto) {

        AuditoriumResourceDto auditorium = auditoriumService.updateAuditorium(id, requestDto);

        return ResponseEntity
                .ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật phòng chiếu thành công", auditorium));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuditorium(@PathVariable Long id) {

        auditoriumService.deleteAuditorium(id);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xóa phòng chiếu thành công", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedAuditoriums() {

        auditoriumService.rollBackDeletedAuditoriums();

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Khôi phục phòng chiếu đã xóa thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditoriumResourceDto>>> getAllAuditoriums() {

        var auditoriums = auditoriumService.getAllAuditoriums();

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy danh sách phòng chiếu thành công", auditoriums));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditoriumResourceDto>> getAuditoriumById(@PathVariable Long id) {

        var auditorium = auditoriumService.getAuditoriumById(id);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy thông tin phòng chiếu thành công", auditorium));
    }
}