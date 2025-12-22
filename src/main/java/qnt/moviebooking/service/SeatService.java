package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.SeatRequestDto;
import qnt.moviebooking.dto.request.SeatsRequestDto;
import qnt.moviebooking.dto.resource.SeatPriceResourceDto;
import qnt.moviebooking.dto.resource.SeatResourceDto;
import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.entity.SeatEntity;
import qnt.moviebooking.entity.SeatPriceEntity;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.AuditoriumRepository;
import qnt.moviebooking.repository.SeatRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final SeatPriceService seatPriceService;

    @Transactional
    public List<SeatResourceDto> createSeats(SeatsRequestDto dto) {

        AuditoriumEntity auditorium = auditoriumRepository.findById(dto.getAuditoriumId())
                .orElseThrow(() -> new NotFoundException("Auditorium not found"));

        SeatPriceEntity seatPrice = seatPriceService.getSeatPriceEntityById(dto.getSeatPriceId());

        List<SeatEntity> existingSeats = seatRepository
                .findAllByAuditoriumIdAndRowChartAndSeatNumberInAndDeletedAtIsNull(
                        dto.getAuditoriumId(), dto.getRowChart(), dto.getSeatNumbers());

        if (!existingSeats.isEmpty()) {
            String duplicateSeats = existingSeats.stream()
                    .map(SeatEntity::getSeatNumber)
                    .collect(Collectors.joining(", "));
            throw new ExistException(
                    "Các ghế sau đã tồn tại trong hàng " + dto.getRowChart() + ": " + duplicateSeats);
        }

        List<SeatEntity> seatEntities = dto.getSeatNumbers().stream().map(seatNumber -> SeatEntity.builder()
                .rowChart(dto.getRowChart())
                .seatNumber(seatNumber)
                .auditorium(auditorium)
                .seatPrice(seatPrice)
                .status(true)
                .build()).toList();

        List<SeatEntity> savedEntities = seatRepository.saveAll(seatEntities);
        return savedEntities.stream().map(this::mapToDto).toList();
    }

    @Transactional
    public SeatResourceDto updateSeat(Long id, SeatRequestDto dto) {
        SeatEntity existingSeat = getSeatEntityById(id);

        SeatPriceEntity seatPrice = seatPriceService.getSeatPriceEntityById(dto.getSeatPriceId());

        if (!existingSeat.getAuditorium().getId().equals(dto.getAuditoriumId())) {
            AuditoriumEntity newAuditorium = auditoriumRepository.findById(dto.getAuditoriumId())
                    .orElseThrow(() -> new NotFoundException("Auditorium not found"));
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
        existingSeat.setSeatPrice(seatPrice);

        SeatEntity savedEntity = seatRepository.save(existingSeat);
        return mapToDto(savedEntity);
    }

    @Transactional
    public void deleteSeat(Long id) {
        SeatEntity seat = getSeatEntityById(id);
        seat.setDeletedAt(LocalDateTime.now());
        seat.setStatus(false);
        seatRepository.save(seat);
    }

    @Transactional
    public void deleteSeats(List<Long> ids) {

        List<SeatEntity> seats = seatRepository.findAllById(ids);

        if (seats.isEmpty()) {
            throw new NotFoundException("Danh sách ghế cần xóa không tồn tại!");
        }

        LocalDateTime now = LocalDateTime.now();

        seats.forEach(seat -> {
            seat.setDeletedAt(now);
            seat.setStatus(false);
        });

        seatRepository.saveAll(seats);
    }

    @Transactional
    public void rollbackDeletedSeat() {
        List<SeatEntity> deletedSeats = seatRepository.findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (deletedSeats.isEmpty()) {
            throw new NotFoundException("Không có ghế nào để khôi phục trong 10 phút qua!");
        }

        for (SeatEntity seat : deletedSeats) {
            boolean isOccupied = seatRepository.existsByAuditoriumIdAndRowChartAndSeatNumberAndDeletedAtIsNull(
                    seat.getAuditorium().getId(), seat.getRowChart(), seat.getSeatNumber());

            if (isOccupied) {
                throw new NotFoundException(
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
                .orElseThrow(() -> new NotFoundException("Ghế không tồn tại với id: " + id));
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
            throw new NotFoundException(
                    "Ghế đã tồn tại trong phòng chiếu với hàng '" + rowChart + "' và số ghế '" + seatNumber + "'.");
        }
    }

    private SeatResourceDto mapToDto(SeatEntity entity) {
        return SeatResourceDto.builder()
                .id(entity.getId())
                .rowChart(entity.getRowChart())
                .seatNumber(entity.getSeatNumber())
                .seatType(SeatPriceResourceDto.builder()
                        .id(entity.getId())
                        .seatType(entity.getSeatPrice().getSeatType())
                        .price(entity.getSeatPrice().getPrice())
                        .createdAt(entity.getCreatedAt())
                        .updatedAt(entity.getUpdatedAt())
                        .build())
                .auditoriumId(entity.getAuditorium().getId())
                .status(entity.isStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}