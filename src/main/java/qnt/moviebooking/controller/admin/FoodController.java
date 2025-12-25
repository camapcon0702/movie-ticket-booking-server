package qnt.moviebooking.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.FoodRequestDto;
import qnt.moviebooking.dto.resource.FoodResourceDto;
import qnt.moviebooking.service.FoodService;

@RestController("AdminFoodController")
@RequestMapping("/admin/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<ApiResponse<FoodResourceDto>> createFood(@RequestBody FoodRequestDto dto) {
        FoodResourceDto response = foodService.createFood(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Food created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResourceDto>> updateFood(@PathVariable Long id,
            @RequestBody FoodRequestDto dto) {
        FoodResourceDto response = foodService.updateFood(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Food updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Food deleted successfully", null));
    }

    @PostMapping("/rollback-deleted")
    public ResponseEntity<ApiResponse<Void>> rollbackDeletedFood() {
        foodService.rollbackDeleteFood();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Food rollback deleted successfully", null));
    }
}
