package qnt.moviebooking.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomoResourceDto {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private String payUrl;
    private String signature;
    private Integer resultCode;
    private String message;
    private String deeplink;
    private String qrCodeUrl;
}
