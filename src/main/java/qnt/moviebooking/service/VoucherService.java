package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.VoucherRequestDto;
import qnt.moviebooking.dto.resource.VoucherResourceDto;
import qnt.moviebooking.entity.VoucherEntity;
import qnt.moviebooking.repository.VoucherRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherService {
    private final VoucherRepository voucherRepository;

    @Transactional
    public VoucherResourceDto createVoucher(VoucherRequestDto dto) {
        validateCode(dto);
        validateExpiryDate(dto);
        validateDiscount(dto);

        VoucherEntity voucher = VoucherEntity
                .builder()
                .code(dto.getCode())
                .discountAmount(dto.getDiscountAmount())
                .discountPercentage(dto.getDiscountPercentage())
                .discountMax(dto.getDiscountMax())
                .active(Optional.ofNullable(dto.isActive()).orElse(
                        true))
                .expiryDate(dto.getExpiryDate())
                .build();

        voucherRepository.save(voucher);

        return mapToDto(voucher);
    }

    @Transactional
    public VoucherResourceDto updateVoucher(Long id, VoucherRequestDto dto) {
        VoucherEntity entity = getVoucherEntityById(id);

        if (!entity.getCode().equals(dto.getCode())) {
            validateCode(dto);
        }

        validateExpiryDate(dto);
        validateDiscount(dto);

        entity.setCode(dto.getCode());
        entity.setDiscountAmount(dto.getDiscountAmount());
        entity.setDiscountPercentage(dto.getDiscountPercentage());
        entity.setDiscountMax(dto.getDiscountMax());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setActive(dto.isActive());

        voucherRepository.save(entity);

        return mapToDto(entity);
    }

    @Transactional
    public void deleteVoucher(Long id) {
        VoucherEntity entity = getVoucherEntityById(id);

        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());

        voucherRepository.save(entity);
    }

    @Transactional
    public void rollBackDeleted() {
        List<VoucherEntity> vouchers = voucherRepository.findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (vouchers.isEmpty()) {
            throw new IllegalArgumentException("Không có voucher nào được xoá vào 10 phút trước");
        }

        for (VoucherEntity voucher : vouchers) {
            voucher.setDeletedAt(null);
        }

        voucherRepository.saveAll(vouchers);
    }

    public List<VoucherResourceDto> getAllVoucher() {
        List<VoucherEntity> vouchers = voucherRepository.findAllByDeletedAtIsNull();

        return vouchers.stream().map(this::mapToDto).toList();
    }

    public List<VoucherResourceDto> getAllVoucherExpired(boolean active) {
        List<VoucherEntity> vouchers = voucherRepository.findAllByDeletedAtIsNullAndActive(active);

        return vouchers.stream().map(this::mapToDto).toList();
    }

    public VoucherResourceDto getVoucherById(Long id) {
        VoucherEntity voucher = getVoucherEntityById(id);

        return mapToDto(voucher);
    }

    public VoucherResourceDto getVoucherByIdForUser(Long id) {
        VoucherEntity voucher = voucherRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Không tồn tại voucher với id: " + id));

        return mapToDto(voucher);
    }

    private VoucherEntity getVoucherEntityById(Long id) {
        return voucherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tồn tại voucher với id: " + id));
    }

    private void validateCode(VoucherRequestDto dto) {
        if (voucherRepository.existsByCodeAndDeletedAtIsNull(dto.getCode())) {
            throw new IllegalArgumentException("Code đã tồn tại: " + dto.getCode());
        }
    }

    private void validateExpiryDate(VoucherRequestDto dto) {
        if (!dto.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngày hết hạn phải sau ngày tạo");
        }
    }

    private void validateDiscount(VoucherRequestDto dto) {
        Double amount = dto.getDiscountAmount();
        Double percent = dto.getDiscountPercentage();
        Double max = dto.getDiscountMax();

        if (amount == null && percent == null) {
            throw new IllegalArgumentException("Phải có một trong hai loại giảm giá: Amount hoặc Percent");
        }

        if (amount != null && percent != null) {
            throw new IllegalArgumentException("Chỉ có một trong hai loại giảm giá: Amount hoặc Percent");
        }

        if (amount != null) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Số tiền giảm giá phải lớn hơn 0");
            }
        }

        if (percent != null) {
            if (percent <= 0 || percent > 100) {
                throw new IllegalArgumentException("Số phần trăm giảm giá phải trong khoảng (0, 100]");
            }
        }

        if (max != null && max <= 0) {
            throw new IllegalArgumentException("Số tiền giảm gái tối đa phải lớn hơn 0");
        }
    }

    private VoucherResourceDto mapToDto(VoucherEntity entity) {
        return VoucherResourceDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discountAmount(entity.getDiscountAmount())
                .discountPercentage(entity.getDiscountPercentage())
                .discountMax(entity.getDiscountMax())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
