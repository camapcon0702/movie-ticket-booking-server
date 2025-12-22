package qnt.moviebooking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.entity.PaymentEntity;
import qnt.moviebooking.enums.BookingEnums;
import qnt.moviebooking.enums.PaymentEnums;
import qnt.moviebooking.repository.BookingRepository;
import qnt.moviebooking.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MomoService momoService;
    private final BookingRepository bookingRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String createMomoPayment(Long bookingId) {
        BookingEntity booking = bookingRepository.findByIdAndDeletedAtIsNullAndStatus(bookingId,
                BookingEnums.PENDING)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String orderId = "Momo_" + booking.getId() + "_" +
                System.currentTimeMillis();

        Map<String, Long> extra = Map.of("bookingId", booking.getId());

        String extraData;
        try {
            extraData = Base64.getEncoder()
                    .encodeToString(objectMapper.writeValueAsBytes(extra));
        } catch (JsonProcessingException e) {
            extraData = "";
        }

        PaymentEntity payment = PaymentEntity.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .method("MOMO")
                .orderId(orderId)
                .extraData(extraData)
                .status(PaymentEnums.PENDING)
                .resultCode(null)
                .build();
        paymentRepository.save(payment);

        log.info("🔵 Tạo payment: orderId={}, amount={}", orderId,
                booking.getTotalAmount());

        String payUrl = momoService.createPayment(
                payment.getAmount().longValueExact(),
                orderId,
                "Thanh toan ve phim - Booking #" + booking.getId(), // Bỏ dấu tiếng Việt
                "captureWallet",
                extraData).getPayUrl();

        return payUrl;
    }

    public void confirmMomoSuccess(String orderId, String transId, String resultCode) {
        PaymentEntity payment = paymentRepository.findByOrderIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + orderId));

        if (payment.getStatus() == PaymentEnums.PAID) {
            return;
        }

        payment.setTransactionId(transId);
        payment.setResultCode(Integer.parseInt(resultCode));
        payment.setStatus(PaymentEnums.PAID);
        payment.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        BookingEntity booking = payment.getBooking();
        booking.setStatus(BookingEnums.SUCCESS);
        bookingRepository.save(booking);

    }

    public void confirmMomoFailed(String orderId, String resultCode, String message) {
        PaymentEntity payment = paymentRepository.findByOrderIdAndDeletedAtIsNull(orderId).orElse(null);
        if (payment != null && payment.getStatus() != PaymentEnums.PAID) {
            payment.setStatus(PaymentEnums.FAILED);
            payment.setResultCode(Integer.parseInt(resultCode));
            paymentRepository.save(payment);

        }
    }

    public Integer createpaymentOffline(Long bookingId) {
        try {
            BookingEntity booking = bookingRepository
                    .findByIdAndDeletedAtIsNullAndStatus(bookingId, BookingEnums.PENDING)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            PaymentEntity payment = PaymentEntity.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .method("CASH") // tiền mặt
                    .status(PaymentEnums.SUCCESS)
                    .build();
            paymentRepository.save(payment);

            return 0;
        } catch (Exception e) {
            return 1;
        }
    }
}