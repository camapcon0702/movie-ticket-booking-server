package qnt.moviebooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import qnt.moviebooking.dto.request.MomoRequestDto;
import qnt.moviebooking.dto.resource.MomoResourceDto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class MomoService {

    private final String partnerCode;
    private final String accessKey;
    private final String secretKey;
    private final String endpoint;
    private final String redirectUrl;
    private final String ipnUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public MomoService(
            @Value("${momo.partner-code}") String partnerCode,
            @Value("${momo.access-key}") String accessKey,
            @Value("${momo.secret-key}") String secretKey,
            @Value("${momo.endpoint}") String endpoint,
            @Value("${momo.redirect-url}") String redirectUrl,
            @Value("${momo.ipn-url}") String ipnUrl) {
        this.partnerCode = partnerCode;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = endpoint;
        this.redirectUrl = redirectUrl;
        this.ipnUrl = ipnUrl;
    }

    public MomoResourceDto createPayment(Long amount,
                                         String orderId,
                                         String orderInfo,
                                         String requestType,
                                         String extraData) {

        String requestId = String.valueOf(System.currentTimeMillis());

        // ✅ Đảm bảo không có khoảng trắng hay ký tự thừa
        String cleanedRequestType = requestType != null ? requestType.trim() : "captureWallet";
        String cleanedExtraData = extraData != null ? extraData : "";

        // ✅ Tạo raw signature theo đúng thứ tự MoMo yêu cầu
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + cleanedExtraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + cleanedRequestType;

        // ✅ Tạo signature bằng HMAC-SHA256
        String signature = hmacSHA256(rawSignature, secretKey);

        MomoRequestDto request = MomoRequestDto.builder()
                .partnerCode(partnerCode)
                .accessKey(accessKey)
                .requestId(requestId)
                .amount(amount)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(redirectUrl)
                .ipnUrl(ipnUrl)
                .requestType(cleanedRequestType)
                .extraData(cleanedExtraData)
                .signature(signature)
                .build();

        log.info("=== MoMo Payment Request ===");
        log.info("OrderId: {}", orderId);
        log.info("Amount: {}", amount);
        log.info("RequestType: [{}]", cleanedRequestType);
        log.info("Raw signature: {}", rawSignature);
        log.info("Signature (HMAC-SHA256): {}", signature);
        log.info("===========================");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MomoRequestDto> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MomoResourceDto> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    MomoResourceDto.class
            );

            MomoResourceDto result = response.getBody();
            if (result != null && result.getResultCode() == 0) {
                log.info("✅ Tạo MoMo payment thành công! payUrl: {}", result.getPayUrl());
            } else {
                log.error("❌ Tạo MoMo payment thất bại: {}", result);
            }

            return result;
        } catch (Exception e) {
            log.error("❌ Exception khi gọi MoMo API: ", e);
            throw e;
        }
    }
    private String hmacSHA256(String data, String secretKey) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

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