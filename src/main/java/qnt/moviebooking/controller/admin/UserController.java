package qnt.moviebooking.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.UserResourceDto;
import qnt.moviebooking.service.UserService;

@RestController("AdminUserController")
@RequestMapping("/v1.0/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResourceDto>>> getAllUser() {

        List<UserResourceDto> users = userService.getAllUserNoDelete();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Danh sách tài khoản!", users));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestParam String email) {

        userService.deleteUser(email);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xoá tài khoản thành công!", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollbackDeleted() {

        userService.rollbackDeletedUser();

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Khôi phục tài khoản thành công!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResourceDto>> getUserById(@PathVariable Long id) {

        UserResourceDto user = userService.getUserById(id);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Thông tin tài khoản!", user));
    }
}