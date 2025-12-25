package qnt.moviebooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.FoodEntity;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {
    Optional<FoodEntity> findByNameAndDeletedAtIsNull(String name);

    Optional<FoodEntity> findByIdAndDeletedAtIsNull(Long id);

    List<FoodEntity> findAllByDeletedAtIsNull();

    List<FoodEntity> findAllByDeletedAtAfter(LocalDateTime time);
}
