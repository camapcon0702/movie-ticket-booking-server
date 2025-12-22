package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.AuditoriumRequestDto;
import qnt.moviebooking.dto.resource.AuditoriumResourceDto;
import qnt.moviebooking.dto.resource.SeatResourceDto;
import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.AuditoriumRepository;

@Service
@RequiredArgsConstructor
public class AuditoriumService {
    private final AuditoriumRepository auditoriumRepository;
    private final SeatService seatService;

    @Transactional
    public AuditoriumResourceDto createAuditorium(AuditoriumRequestDto requestDto) {
        validateTitle(requestDto.getName(), null);

        AuditoriumEntity entity = mapToEntity(requestDto);
        AuditoriumEntity savedEntity = auditoriumRepository.save(entity);
        return mapToDto(savedEntity);
    }

    @Transactional
    public AuditoriumResourceDto updateAuditorium(Long id, AuditoriumRequestDto requestDto) {
        AuditoriumEntity existingAuditorium = getAuditoriumEntityById(id);

        validateTitle(requestDto.getName(), existingAuditorium.getId());

        AuditoriumEntity updatedAuditorium = mapToEntity(requestDto);
        updatedAuditorium.setId(existingAuditorium.getId());
        AuditoriumEntity savedEntity = auditoriumRepository.save(updatedAuditorium);
        return mapToDto(savedEntity);
    }

    @Transactional
    public void deleteAuditorium(Long id) {
        AuditoriumEntity auditorium = getAuditoriumEntityById(id);
        List<SeatResourceDto> seats = seatService.getSeatsByAuditorium(id);

        if (!seats.isEmpty()) {
            List<Long> seatIds = seats.stream().map(SeatResourceDto::getId).toList();

            seatService.deleteSeats(seatIds);
        }

        auditorium.setDeletedAt(LocalDateTime.now());

        auditoriumRepository.save(auditorium);
    }

    @Transactional
    public void rollBackDeletedAuditoriums() {
        var deletedAuditoriums = auditoriumRepository
                .findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));
        if (deletedAuditoriums.isEmpty()) {
            throw new NotFoundException("Không có phòng chiếu nào để khôi phục!");
        }
        deletedAuditoriums.forEach(auditorium -> auditorium.setDeletedAt(null));
        auditoriumRepository.saveAll(deletedAuditoriums);
    }

    public AuditoriumEntity getAuditoriumEntityById(Long id) {
        return auditoriumRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Phòng chiếu không tồn tại với id: " + id));
    }

    public AuditoriumResourceDto getAuditoriumById(Long id) {
        AuditoriumEntity auditorium = getAuditoriumEntityById(id);
        return mapToDto(auditorium);
    }

    public List<AuditoriumResourceDto> getAllAuditoriums() {
        List<AuditoriumEntity> auditoriums = auditoriumRepository.findByDeletedAtIsNull();
        return auditoriums.stream().map(this::mapToDto).toList();
    }

    public AuditoriumEntity getAuditoriumByName(String name) {
        return auditoriumRepository.findByNameAndDeletedAtIsNull(name)
                .orElseThrow(() -> new NotFoundException("Phòng chiếu không tồn tại với tên: " + name));
    }

    private void validateTitle(String name, Long excludeId) {
        boolean exists = (excludeId == null) ? auditoriumRepository.existsByNameAndDeletedAtIsNull(name)
                : auditoriumRepository.findByNameAndDeletedAtIsNull(name)
                        .filter(a -> !a.getId().equals(excludeId)).isPresent();
        if (exists) {
            throw new ExistException("Tên phòng chiếu đã tồn tại: " + name);
        }
    }

    private AuditoriumResourceDto mapToDto(AuditoriumEntity entity) {
        return AuditoriumResourceDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private AuditoriumEntity mapToEntity(AuditoriumRequestDto dto) {
        return AuditoriumEntity.builder()
                .name(dto.getName())
                .build();
    }
}
