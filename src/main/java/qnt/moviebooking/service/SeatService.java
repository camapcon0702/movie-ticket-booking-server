package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.SeatRequestDto;
import qnt.moviebooking.dto.request.SeatsRequestDto;
import qnt.moviebooking.dto.resource.SeatResourceDto;
import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.entity.SeatEntity;
import qnt.moviebooking.enums.SeatEnums;
import qnt.moviebooking.repository.SeatRepository;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final AuditoriumService auditoriumService;

    public List<SeatResourceDto> createSeats(SeatsRequestDto dto) {

        SeatEnums seatType = parseSeatType(dto.getSeatType());

        AuditoriumEntity auditorium = auditoriumService.getAuditoriumEntityById(dto.getAuditoriumId());

        List<SeatEntity> existingSeats = seatRepository
                .findAllByAuditoriumIdAndRowChartAndSeatNumberInAndDeletedAtIsNull(
                        dto.getAuditoriumId(), dto.getRowChart(), dto.getSeatNumbers());

        if (!existingSeats.isEmpty()) {
            String duplicateSeats = existingSeats.stream()
                    .map(SeatEntity::getSeatNumber)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Các ghế sau đã tồn tại trong hàng " + dto.getRowChart() + ": " + duplicateSeats);
        }

        List<SeatEntity> seatEntities = dto.getSeatNumbers().stream().map(seatNumber -> SeatEntity.builder()
                .rowChart(dto.getRowChart())
                .seatNumber(seatNumber)
                .auditorium(auditorium)
                .seatType(seatType)
                .status(true)
                .build()).toList();

        List<SeatEntity> savedEntities = seatRepository.saveAll(seatEntities);
        return savedEntities.stream().map(this::mapToDto).toList();
    }

    public SeatResourceDto updateSeat(Long id, SeatRequestDto dto) {
        SeatEntity existingSeat = getSeatEntityById(id);
        SeatEnums seatType = parseSeatType(dto.getSeatType());

        if (!existingSeat.getAuditorium().getId().equals(dto.getAuditoriumId())) {
            AuditoriumEntity newAuditorium = auditoriumService.getAuditoriumEntityById(dto.getAuditoriumId());
            existingSeat.setAuditorium(newAuditorium);
        }

        boolean isLocationChanged = !existingSeat.getRowChart().equals(dto.getRowChart())
                || !existingSeat.getSeatNumber().equals(dto.getSeatNumber())
                || !existingSeat.getAuditorium().getId().equals(dto.getAuditoriumId());

        if (isLocationChanged) {
            validateSeatConflict(dto.getAuditoriumId(), dto.getRowChart(), dto.getSeatNumber());
        }

        existingSeat.setRowChart(dto.getRowChart());
        existingSeat.setSeatNumber(dto.getSeatNumber());
        existingSeat.setSeatType(seatType);

        SeatEntity savedEntity = seatRepository.save(existingSeat);
        return mapToDto(savedEntity);
    }

    public void deleteSeat(Long id) {
        SeatEntity seat = getSeatEntityById(id);
        seat.setDeletedAt(LocalDateTime.now());
        seat.setStatus(false);
        seatRepository.save(seat);
    }

    public void rollbackDeletedSeat() {
        List<SeatEntity> deletedSeats = seatRepository.findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (deletedSeats.isEmpty()) {
            throw new IllegalArgumentException("Không có ghế nào để khôi phục trong 10 phút qua!");
        }

        for (SeatEntity seat : deletedSeats) {
            boolean isOccupied = seatRepository.existsByAuditoriumIdAndRowChartAndSeatNumberAndDeletedAtIsNull(
                    seat.getAuditorium().getId(), seat.getRowChart(), seat.getSeatNumber());

            if (isOccupied) {
                throw new IllegalArgumentException(
                        String.format("Không thể khôi phục ghế %s-%s vì vị trí này đã có ghế mới.",
                                seat.getRowChart(), seat.getSeatNumber()));
            }

            seat.setDeletedAt(null);
            seat.setStatus(true);
        }

        seatRepository.saveAll(deletedSeats);
    }

    public SeatResourceDto getSeatById(Long id) {
        return mapToDto(getSeatEntityById(id));
    }

    public SeatEntity getSeatEntityById(Long id) {
        return seatRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Ghế không tồn tại với id: " + id));
    }

    public List<SeatResourceDto> getSeatsByAuditorium(Long auditoriumId) {
        return seatRepository.findAllByAuditoriumIdAndDeletedAtIsNull(auditoriumId)
                .stream().map(this::mapToDto).toList();
    }

    public List<SeatResourceDto> getAllSeats() {
        return seatRepository.findAllByDeletedAtIsNull()
                .stream().map(this::mapToDto).toList();
    }

    public List<SeatResourceDto> getSeatsByIdsAndAuditorium(Long auditoriumId, List<Long> seatIds) {
        return seatRepository.findAllByAuditoriumIdAndIdInAndDeletedAtIsNull(auditoriumId, seatIds)
                .stream().map(this::mapToDto).toList();
    }

    private void validateSeatConflict(Long auditoriumId, String rowChart, String seatNumber) {
        boolean exists = seatRepository.existsByAuditoriumIdAndRowChartAndSeatNumberAndDeletedAtIsNull(
                auditoriumId, rowChart, seatNumber);
        if (exists) {
            throw new IllegalArgumentException(
                    "Ghế đã tồn tại trong phòng chiếu với hàng '" + rowChart + "' và số ghế '" + seatNumber + "'.");
        }
    }

    private SeatEnums parseSeatType(String seatTypeStr) {
        try {
            return SeatEnums.valueOf(seatTypeStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Loại ghế không hợp lệ: " + seatTypeStr);
        }
    }

    private SeatResourceDto mapToDto(SeatEntity entity) {
        return SeatResourceDto.builder()
                .id(entity.getId())
                .rowChart(entity.getRowChart())
                .seatNumber(entity.getSeatNumber())
                .seatType(entity.getSeatType().name())
                .auditoriumId(entity.getAuditorium().getId())
                .status(entity.isStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}