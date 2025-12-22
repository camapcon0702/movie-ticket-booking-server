package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.VoucherRequestDto;
import qnt.moviebooking.dto.resource.VoucherResourceDto;
import qnt.moviebooking.service.VoucherService;

@RestController("AdminVoucherController")
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
public class VocherController {
    private final VoucherService voucherService;

    @PostMapping
    public ResponseEntity<ApiResponse<VoucherResourceDto>> createVoucher(@RequestBody VoucherRequestDto request) {
        VoucherResourceDto response = voucherService.createVoucher(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo voucher thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResourceDto>> updateVoucher(@PathVariable Long id,
            @RequestBody VoucherRequestDto request) {
        VoucherResourceDto response = voucherService.updateVoucher(id, request);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật voucher thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);

        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "Xoá voucher thành công", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollbackDeleted() {
        voucherService.rollBackDeleted();

        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "Hoàn tác voucher thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherResourceDto>>> getAllVouchers() {
        List<VoucherResourceDto> response = voucherService.getAllVoucher();

        return ResponseEntity.ok().body(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy thông tin voucher thành công", response));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<VoucherResourceDto>>> getAllVouchersActive(@RequestParam boolean active) {
        List<VoucherResourceDto> response = voucherService.getAllVoucherExpired(active);

        return ResponseEntity.ok().body(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy thông tin voucher thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResourceDto>> getVoucherById(@PathVariable Long id) {
        VoucherResourceDto response = voucherService.getVoucherById(id);

        return ResponseEntity.ok().body(new ApiResponse<>(
                HttpStatus.OK.value(), "Lấy thông tin voucher thành công", response));
    }
}
