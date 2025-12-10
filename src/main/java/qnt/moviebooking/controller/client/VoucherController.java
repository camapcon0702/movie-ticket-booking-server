package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.VoucherResourceDto;
import qnt.moviebooking.service.VoucherService;

@RestController("ClientVoucherController")
@RequestMapping("/v1.0/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService voucherService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherResourceDto>>> getAllVouchersForUser() {
        List<VoucherResourceDto> response = voucherService.getAllVoucherExpired(true);

        return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thông tin voucher thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VoucherResourceDto>> getVoucherForUser(@PathVariable Long id) {
        VoucherResourceDto response = voucherService.getVoucherByIdForUser(id);

        return ResponseEntity.ok().body(new ApiResponse<>(true, "Lấy thông tin voucher thành công", response));
    }
}
