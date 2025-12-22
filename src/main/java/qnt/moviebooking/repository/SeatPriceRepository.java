package qnt.moviebooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import qnt.moviebooking.entity.SeatPriceEntity;

@Repository
public interface SeatPriceRepository extends JpaRepository<SeatPriceEntity, Long> {
    Optional<SeatPriceEntity> findBySeatType(String type);
}
