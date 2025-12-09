package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.SeatEntity;


@Repository
public interface SeatRepository extends JpaRepository<SeatEntity,Long> {
}
