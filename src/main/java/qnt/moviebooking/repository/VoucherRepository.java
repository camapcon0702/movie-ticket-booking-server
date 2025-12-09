package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.VoucherEntity;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity,Long> {
}
