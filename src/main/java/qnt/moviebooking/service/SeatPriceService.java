package qnt.moviebooking.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.SeatPriceRequestDto;
import qnt.moviebooking.dto.resource.SeatPriceResourceDto;
import qnt.moviebooking.entity.SeatPriceEntity;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.SeatPriceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatPriceService {
    private final SeatPriceRepository seatPriceRepository;

    @Transactional
    public SeatPriceResourceDto createSeatPrice(SeatPriceRequestDto dto) {
        String seatType = dto.getSeatType().trim().toUpperCase();

        validateSeatType(seatType, null);

        SeatPriceEntity seatPrice = SeatPriceEntity.builder()
                .seatType(seatType)
                .price(dto.getPrice())
                .build();

        seatPriceRepository.save(seatPrice);

        return mapToResource(seatPrice);
    }

    public List<SeatPriceResourceDto> getAllSeatPrice() {
        List<SeatPriceEntity> seatPrices = seatPriceRepository.findAll();

        return seatPrices.stream().map(this::mapToResource).toList();
    }

    public SeatPriceEntity getSeatPriceEntityById(Long id) {
        SeatPriceEntity seatPrice = seatPriceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại loại ghế với id: " + id));
        return seatPrice;
    }

    public SeatPriceResourceDto getSeatTypeById(Long id) {
        SeatPriceEntity seatPrice = seatPriceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại loại ghế với id: " + id));
        return mapToResource(seatPrice);
    }

    @Transactional
    public SeatPriceResourceDto updateSeatPrice(Long id, SeatPriceRequestDto dto) {
        String seatType = dto.getSeatType().trim().toUpperCase();
        SeatPriceEntity seatPrice = getSeatPriceEntityById(id);

        if (!seatType.equals(seatPrice.getSeatType())) {
            validateSeatType(seatType, id);
            seatPrice.setSeatType(seatType);
        }

        seatPrice.setPrice(dto.getPrice());

        seatPriceRepository.save(seatPrice);

        return mapToResource(seatPrice);
    }

    @Transactional
    public void deleteSeatPrice(Long id) {
        SeatPriceEntity seatPrice = getSeatPriceEntityById(id);

        seatPriceRepository.delete(seatPrice);
    }

    private void validateSeatType(String type, Long id) {
        seatPriceRepository.findBySeatType(type).ifPresent(existing -> {
            if (id == null || !existing.getId().equals(id)) {
                throw new ExistException("Đã tồn tại loại ghế này: " + type);
            }
        });
    }

    private SeatPriceResourceDto mapToResource(SeatPriceEntity entity) {
        return SeatPriceResourceDto.builder()
                .id(entity.getId())
                .seatType(entity.getSeatType())
                .price(entity.getPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
