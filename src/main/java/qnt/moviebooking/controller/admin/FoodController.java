package qnt.moviebooking.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.request.FoodRequestDto;
import qnt.moviebooking.dto.resource.FoodResourceDto;
import qnt.moviebooking.service.FoodService;

import java.util.List;

@RestController("AdminFoodController")
@RequestMapping("/v1.0/admin/foods")
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

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<FoodResourceDto>>> getAllFood() {
        return  ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All food resources successfully", foodService.getAllFood()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResourceDto>> getFood(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Food updated successfully", foodService.getFoodById(id)));
    }
}
