package qnt.moviebooking.dto.resource.Booking;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qnt.moviebooking.dto.resource.ServiceResourceDto;
import qnt.moviebooking.dto.resource.TicketResourceDto;
import qnt.moviebooking.dto.resource.VoucherResourceDto;
import qnt.moviebooking.enums.BookingEnums;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetailResourceDto {
    long Id;
    long userId;
    String FullName;
    String Email;
    String Phone;
    private LocalDateTime bookingTime;
    private BigDecimal total;
    private BookingEnums status;
    private String nameMovie;
    private String urlMovie;
    private VoucherResourceDto voucher;
    private LocalDateTime showtimeMovie;
    private List<TicketResourceDto> tickets;
    private List<ServiceResourceDto> services;
}
