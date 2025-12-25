package qnt.moviebooking.controller.client;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.FoodResourceDto;
import qnt.moviebooking.service.FoodService;

@RestController("ClientFoodController")
@RequestMapping("/v1.0/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodResourceDto>>> getAllFood() {
        var response = foodService.getAllFood();
        return ResponseEntity.ok(new ApiResponse<>(200, "Get all foods successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodResourceDto>> getFoodById(@PathVariable Long id) {
        FoodResourceDto response = foodService.getFoodById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Get food by id successfully", response));
    }
}
