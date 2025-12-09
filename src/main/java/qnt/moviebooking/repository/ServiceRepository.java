package qnt.moviebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qnt.moviebooking.entity.ServiceEntity;

import java.util.List;
import java.util.Set;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity,Long> {
   List<ServiceEntity> findAllByIdInAndDeletedAtIsNull(List<Long> id);
   List<ServiceEntity> findAllByIdInAndDeletedAtIsNull(Set<Long> ids);
}
