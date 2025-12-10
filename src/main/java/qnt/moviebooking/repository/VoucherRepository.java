package qnt.moviebooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import qnt.moviebooking.entity.VoucherEntity;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    boolean existsByCodeAndDeletedAtIsNull(String code);

    Optional<VoucherEntity> findByIdAndDeletedAtIsNull(Long id);

    List<VoucherEntity> findAllByDeletedAtAfter(LocalDateTime time);

    List<VoucherEntity> findAllByDeletedAtIsNull();

    List<VoucherEntity> findAllByDeletedAtIsNullAndActive(boolean active);

    Optional<VoucherEntity> findByIdAndActiveTrue(Long id);
}
