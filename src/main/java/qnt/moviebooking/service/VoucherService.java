package qnt.moviebooking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.VoucherRequestDto;
import qnt.moviebooking.dto.resource.VoucherResourceDto;
import qnt.moviebooking.entity.VoucherEntity;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
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
            throw new NotFoundException("Không có voucher nào được xoá vào 10 phút trước");
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
                .orElseThrow(() -> new NotFoundException(
                        "Không tồn tại voucher với id: " + id));

        return mapToDto(voucher);
    }

    public VoucherEntity getVoucherEntityById(Long id) {
        return voucherRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại voucher với id: " + id));
    }

    public BigDecimal applyVoucher(VoucherEntity voucher, BigDecimal totalPrice) {
        LocalDateTime now = LocalDateTime.now();

        if (!voucher.isActive()) {
            throw new NotFoundException("Voucher không còn hoạt động");
        }

        if (voucher.getExpiryDate().isBefore(now)) {
            throw new NotFoundException("Voucher đã hết hạn");
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (voucher.getDiscountAmount() != null) {
            discount = voucher.getDiscountAmount();
        } else if (voucher.getDiscountPercentage() != null) {
            discount = totalPrice
                    .multiply(BigDecimal.valueOf(voucher.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100));
            if (voucher.getDiscountMax() != null) {
                BigDecimal maxDiscount = voucher.getDiscountMax();
                if (discount.compareTo(maxDiscount) > 0) {
                    discount = maxDiscount;
                }
            }
        }

        if (discount.compareTo(totalPrice) > 0) {
            discount = totalPrice;
        }

        return totalPrice.subtract(discount);
    }

    private void validateCode(VoucherRequestDto dto) {
        if (voucherRepository.existsByCodeAndDeletedAtIsNull(dto.getCode())) {
            throw new ExistException("Code đã tồn tại: " + dto.getCode());
        }
    }

    private void validateExpiryDate(VoucherRequestDto dto) {
        if (!dto.getExpiryDate().isAfter(LocalDateTime.now())) {
            throw new NotFoundException("Ngày hết hạn phải sau ngày tạo");
        }
    }

    private void validateDiscount(VoucherRequestDto dto) {
        BigDecimal amount = dto.getDiscountAmount();
        Double percent = dto.getDiscountPercentage();
        BigDecimal max = dto.getDiscountMax();

        if (amount == null && percent == null) {
            throw new NotFoundException("Phải có một trong hai loại giảm giá: Amount hoặc Percent");
        }

        if (amount != null && percent != null) {
            throw new NotFoundException("Chỉ có một trong hai loại giảm giá: Amount hoặc Percent");
        }

        if (amount != null) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NotFoundException("Số tiền giảm giá phải lớn hơn 0");
            }
        }

        if (percent != null) {
            if (percent <= 0 || percent > 100) {
                throw new NotFoundException("Số phần trăm giảm giá phải trong khoảng (0, 100]");
            }
        }

        if (max != null && max.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NotFoundException("Số tiền giảm gái tối đa phải lớn hơn 0");
        }
    }

    private VoucherResourceDto mapToDto(VoucherEntity entity) {
        return VoucherResourceDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discountAmount(entity.getDiscountAmount())
                .discountPercentage(entity.getDiscountPercentage())
                .discountMax(entity.getDiscountMax())
                .expiryDate(entity.getExpiryDate())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
