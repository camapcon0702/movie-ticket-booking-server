package qnt.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class MomoTestController {

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @GetMapping("/direct-test")
    public ResponseEntity<?> directTest() {
        try {
            String requestId = String.valueOf(System.currentTimeMillis());
            String orderId = "TEST_" + System.currentTimeMillis();
            Long amount = 50000L;
            String orderInfo = "Test Payment";
            String redirectUrl = "http://localhost:8080/api/v1.0/momo/return";
            String ipnUrl = "http://localhost:8080/api/v1.0/momo/ipn";
            String requestType = "captureWallet";
            String extraData = "";

            // ✅ Tạo raw signature theo đúng thứ tự MoMo yêu cầu
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            // ✅ QUAN TRỌNG: Dùng HMAC-SHA256 thay vì SHA256 thông thường
            String signature = hmacSHA256(rawSignature, secretKey);

            log.info("=== DEBUG INFO ===");
            log.info("Partner Code: [{}]", partnerCode);
            log.info("Access Key: [{}]", accessKey);
            log.info("Secret Key length: {}", secretKey.length());
            log.info("Request Type: [{}]", requestType);
            log.info("Raw Signature: {}", rawSignature);
            log.info("Signature (HMAC-SHA256): {}", signature);

            // Tạo request body bằng Map
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("accessKey", accessKey);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", redirectUrl);
            requestBody.put("ipnUrl", ipnUrl);
            requestBody.put("requestType", requestType);
            requestBody.put("extraData", extraData);
            requestBody.put("signature", signature);

            // Serialize to JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(requestBody);
            log.info("Request JSON: {}", jsonBody);

            // Gửi request
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Content-Type", "application/json; charset=UTF-8");
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("✅ Response: {}", response.getBody());
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            log.error("❌ Error: ", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * ✅ Tạo HMAC-SHA256 signature
     */
    private String hmacSHA256(String data, String secretKey) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC-SHA256", e);
        }
    }
}