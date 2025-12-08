package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.AuditoryumRequestDto;
import qnt.moviebooking.dto.resource.AuditoriumResourceDto;
import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.repository.AuditoriumRepository;

@Service
@RequiredArgsConstructor
public class AuditoriumService {
    private final AuditoriumRepository auditoriumRepository;

    public AuditoriumResourceDto createAuditorium(AuditoryumRequestDto requestDto) {
        validateTitle(requestDto.getName(), null);

        AuditoriumEntity entity = mapToEntity(requestDto);
        AuditoriumEntity savedEntity = auditoriumRepository.save(entity);
        return mapToDto(savedEntity);
    }

    public AuditoriumResourceDto updateAuditorium(Long id, AuditoryumRequestDto requestDto) {
        AuditoriumEntity existingAuditorium = getAuditoriumEntityById(id);

        validateTitle(requestDto.getName(), existingAuditorium.getId());

        AuditoriumEntity updatedAuditorium = mapToEntity(requestDto);
        updatedAuditorium.setId(existingAuditorium.getId());
        AuditoriumEntity savedEntity = auditoriumRepository.save(updatedAuditorium);
        return mapToDto(savedEntity);
    }

    public void deleteAuditorium(Long id) {
        AuditoriumEntity auditorium = getAuditoriumEntityById(id);
        auditorium.setDeletedAt(LocalDateTime.now());
        auditoriumRepository.save(auditorium);
    }

    public void rollBackDeletedAuditoriums() {
        var deletedAuditoriums = auditoriumRepository
                .findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));
        if (deletedAuditoriums.isEmpty()) {
            throw new IllegalArgumentException("Không có phòng chiếu nào để khôi phục!");
        }
        deletedAuditoriums.forEach(auditorium -> auditorium.setDeletedAt(null));
        auditoriumRepository.saveAll(deletedAuditoriums);
    }

    public AuditoriumEntity getAuditoriumEntityById(Long id) {
        return auditoriumRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Phòng chiếu không tồn tại với id: " + id));
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
                .orElseThrow(() -> new IllegalArgumentException("Phòng chiếu không tồn tại với tên: " + name));
    }

    private void validateTitle(String name, Long excludeId) {
        boolean exists = (excludeId == null) ? auditoriumRepository.existsByNameAndDeletedAtIsNull(name)
                : auditoriumRepository.findByNameAndDeletedAtIsNull(name)
                        .filter(a -> !a.getId().equals(excludeId)).isPresent();
        if (exists) {
            throw new IllegalArgumentException("Tên phòng chiếu đã tồn tại: " + name);
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

    private AuditoriumEntity mapToEntity(AuditoryumRequestDto dto) {
        return AuditoriumEntity.builder()
                .name(dto.getName())
                .build();
    }
}
