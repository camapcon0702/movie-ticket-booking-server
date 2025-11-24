package qnt.moviebooking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.AuditoryumRequestDto;
import qnt.moviebooking.dto.resource.AuditoriumResourceDto;
import qnt.moviebooking.service.AuditoriumService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/auditoriums")
public class AuditoriumController {
    private final AuditoriumService auditoriumService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuditoriumResourceDto>> createAuditorium(
            @RequestBody AuditoryumRequestDto requestDto) {
        try {
            AuditoriumResourceDto auditorium = auditoriumService.createAuditorium(requestDto);

            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo phòng chiếu thành công", auditorium));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi tạo phòng chiếu", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditoriumResourceDto>> updateAuditorium(@PathVariable Long id,
            @RequestBody AuditoryumRequestDto requestDto) {
        try {
            AuditoriumResourceDto auditorium = auditoriumService.updateAuditorium(id, requestDto);

            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật phòng chiếu thành công", auditorium));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi cập nhật phòng chiếu", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuditorium(@PathVariable Long id) {
        try {
            auditoriumService.deleteAuditorium(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa phòng chiếu thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi xóa phòng chiếu", null));
        }
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollBackDeletedAuditoriums() {
        try {
            auditoriumService.rollBackDeletedAuditoriums();
            return ResponseEntity.ok(new ApiResponse<>(true, "Khôi phục phòng chiếu đã xóa thành công", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi khôi phục phòng chiếu đã xóa", null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditoriumResourceDto>>> getAllAuditoriums() {
        try {
            var auditoriums = auditoriumService.getAllAuditoriums();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phòng chiếu thành công", auditoriums));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy danh sách phòng chiếu", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditoriumResourceDto>> getAuditoriumById(@PathVariable Long id) {
        try {
            var auditorium = auditoriumService.getAuditoriumById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phòng chiếu thành công", auditorium));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Đã xảy ra lỗi khi lấy thông tin phòng chiếu", null));
        }
    }
}
