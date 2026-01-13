package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.FoodRequestDto;
import qnt.moviebooking.dto.resource.FoodResourceDto;
import qnt.moviebooking.entity.FoodEntity;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.FoodRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodService {
    private final FoodRepository foodRepository;

    @Transactional
    public FoodResourceDto createFood(FoodRequestDto dto) {
        validateName(dto.getName(), null);

        FoodEntity food = FoodEntity.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .imgUrl(dto.getImgUrl())
                .build();
        foodRepository.save(food);

        return mapToResource(food);
    }

    public FoodResourceDto getFoodById(Long id) {
        FoodEntity food = getFoodEntityById(id);
        return mapToResource(food);
    }

    public FoodEntity getFoodEntityById(Long id) {
        FoodEntity food = foodRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Food with id " + id + " not found."));
        return food;
    }

    public List<FoodResourceDto> getAllFood() {
        List<FoodEntity> foods = foodRepository.findAllByDeletedAtIsNull();
        return foods.stream().map(this::mapToResource).toList();
    }

    @Transactional
    public FoodResourceDto updateFood(Long id, FoodRequestDto dto) {
        FoodEntity food = getFoodEntityById(id);

        validateName(dto.getName(), id);
        food.setName(dto.getName());
        food.setPrice(dto.getPrice());
        food.setImgUrl(dto.getImgUrl());

        foodRepository.save(food);

        return mapToResource(food);
    }

    @Transactional
    public void deleteFood(Long id) {
        FoodEntity food = getFoodEntityById(id);
        food.setDeletedAt(LocalDateTime.now());
        foodRepository.save(food);
    }

    public void rollbackDeleteFood() {
        List<FoodEntity> foods = foodRepository.findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (foods.isEmpty()) {
            throw new NotFoundException("No foods found to rollback deletion.");
        }

        for (FoodEntity food : foods) {
            food.setDeletedAt(null);
        }
        foodRepository.saveAll(foods);
    }

    private void validateName(String name, Long id) {
        foodRepository.findByNameAndDeletedAtIsNull(name).ifPresent(existingFood -> {
            if (id == null || !existingFood.getId().equals(id)) {
                throw new ExistException("Food with name '" + name + "' already exists.");
            }
        });
    }

    private FoodResourceDto mapToResource(FoodEntity food) {
        return FoodResourceDto.builder()
                .id(food.getId())
                .name(food.getName())
                .price(food.getPrice())
                .imgUrl(food.getImgUrl())
                .createdAt(food.getCreatedAt())
                .updatedAt(food.getUpdatedAt())
                .build();
    }
}
