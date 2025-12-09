package qnt.moviebooking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qnt.moviebooking.service.PaymentService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/booking/{bookingId}/momo")
    public ResponseEntity<?> payWithMomo(@PathVariable Long bookingId) {
        try {
            String payUrl = paymentService.createMomoPayment(bookingId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "payUrl", payUrl,
                    "message", "Vui lòng quét QR code để thanh toán"
            ));
        } catch (Exception ex) {
            log.error("Error creating MoMo payment for booking {}: ", bookingId, ex);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/booking/{bookingId}/Cash")
    public ResponseEntity<?> payWithCash(@PathVariable Long bookingId) {
        try {
            Integer result = paymentService.createpaymentOffline(bookingId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "result", result,
                    "message", "Vui lòng quét QR code để thanh toán"
            ));
        } catch (Exception ex) {
            log.error("Error creating MoMo payment for booking {}: ", bookingId, ex);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }


    @GetMapping("/momo/return")
    public String momoReturn(@RequestParam Map<String, String> params) {
        String resultCode = params.get("resultCode");
        String orderId = params.get("orderId");
        if ("0".equals(resultCode)) {
            return "redirect:/booking-success.html?orderId=" + orderId;
        } else {
            return "redirect:/booking-failed.html?error=" + resultCode;
        }
    }

    @PostMapping("/momo/ipn")
    public ResponseEntity<?> momoIpn(@RequestBody Map<String, Object> body) {
        try {
            String orderId = String.valueOf(body.get("orderId"));
            String resultCode = String.valueOf(body.get("resultCode"));
            String transId = String.valueOf(body.get("transId"));

            if ("0".equals(resultCode)) {
                paymentService.confirmMomoSuccess(orderId, transId, resultCode);
            } else {
                String message = String.valueOf(body.get("message"));
                paymentService.confirmMomoFailed(orderId, resultCode, message);
            }
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "IPN received"
            ));
        } catch (Exception ex) {
            log.error("Error processing MoMo IPN: ", ex);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", ex.getMessage()
            ));
        }
    }
}