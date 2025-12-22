package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
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

@RestController("AdminRoleController")
@RequestMapping("admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResourceDto>> createRole(@RequestBody RoleRequestDto request) {

        RoleResourceDto createdRole = roleService.createRole(request);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.CREATED.value(), "Tạo role thành công!", createdRole));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResourceDto>>> getAllRoles() {

        List<RoleResourceDto> roles = roleService.getAllRoles();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách role thành công!", roles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResourceDto>> getRoleById(@PathVariable Long id) {

        RoleResourceDto role = roleService.getRoleById(id);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thông tin role thành công!", role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResourceDto>> updateRole(@PathVariable Long id,
            @RequestBody RoleRequestDto request) {

        RoleResourceDto updatedRole = roleService.updateRole(id, request);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật role thành công!", updatedRole));
    }
}