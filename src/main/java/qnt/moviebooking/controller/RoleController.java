package qnt.moviebooking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.RoleRequestDto;
import qnt.moviebooking.dto.resource.RoleResourceDto;
import qnt.moviebooking.service.RoleService;

@RestController
@RequestMapping("admin/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResourceDto>> createRole(@RequestBody RoleRequestDto request) {
        try {
            RoleResourceDto createdRole = roleService.createRole(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo role thành công!", createdRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi tạo role", null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResourceDto>>> getAllRoles() {
        try {
            List<RoleResourceDto> roles = roleService.getAllRoles();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách role thành công!", roles));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi lấy danh sách role", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResourceDto>> getRoleById(@PathVariable Long id) {
        try {
            RoleResourceDto role = roleService.getRoleById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin role thành công!", role));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi lấy role", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResourceDto>> updateRole(@PathVariable Long id,
            @RequestBody RoleRequestDto request) {
        try {
            RoleResourceDto updatedRole = roleService.updateRole(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật role thành công!", updatedRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "Có lỗi xảy ra khi cập nhật role", null));
        }
    }
}
