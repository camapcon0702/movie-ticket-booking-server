package qnt.moviebooking.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qnt.moviebooking.dto.request.BookingRequestDto;
import qnt.moviebooking.dto.resource.BookingResourceDto;
import qnt.moviebooking.dto.resource.TicketResourceDto;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.entity.SeatEntity;
import qnt.moviebooking.entity.ShowtimeEntity;
import qnt.moviebooking.entity.TicketEntity;
import qnt.moviebooking.entity.UserEntity;
import qnt.moviebooking.entity.VoucherEntity;
import qnt.moviebooking.enums.BookingEnums;
import qnt.moviebooking.enums.TicketEnums;
import qnt.moviebooking.exception.BadRequestException;
import qnt.moviebooking.repository.BookingRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ShowtimeService showtimeService;
    private final VoucherService voucherService;
    private final TicketService ticketService;
    private final UserService userService;
    private final SeatService seatService;

    @Transactional
    public BookingResourceDto createBooking(BookingRequestDto dto) {

        UserEntity user = userService.getCurrentUser();
        ShowtimeEntity showtime = showtimeService.getShowtimeEntityById(dto.getShowtimeId());

        VoucherEntity voucher = null;
        if (dto.getVoucherId() != null) {
            voucher = voucherService.getVoucherEntityById(dto.getVoucherId());
        }

        BookingEntity booking = BookingEntity.builder()
                .user(user)
                .showtime(showtime)
                .status(BookingEnums.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (Long seatId : dto.getSeatId()) {
            SeatEntity seat = seatService.getSeatEntityById(seatId);

            if (!seat.getAuditorium().getId()
                    .equals(showtime.getAuditorium().getId())) {
                throw new BadRequestException("Ghế không thuộc phòng chiếu");
            }

            if (ticketService.isSeatBooked(seatId, showtime.getId())) {
                throw new BadRequestException(
                        "Ghế " + seat.getRowChart() + seat.getSeatNumber() + " đã được giữ");
            }

            BigDecimal price = seat.getSeatPrice().getPrice();

            TicketEntity ticket = TicketEntity.builder()
                    .booking(booking)
                    .seat(seat)
                    .price(price)
                    .status(TicketEnums.HELD)
                    .build();

            booking.getTickets().add(ticket);
            total = total.add(price);
        }

        if (voucher != null) {
            total = voucherService.applyVoucher(voucher, total);
            booking.setVoucher(voucher);
        }

        booking.setTotalAmount(total);

        bookingRepository.save(booking);

        return mapToResource(booking);
    }

    private BookingResourceDto mapToResource(BookingEntity booking) {
        return BookingResourceDto.builder()
                .id(booking.getId())
                .total(booking.getTotalAmount())
                .status(booking.getStatus())
                .startTime(booking.getShowtime().getStartTime())
                .tickets(booking.getTickets().stream().map(this::toTicketResource).toList())
                .nameMovie(booking.getShowtime().getMovie().getTitle())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    private TicketResourceDto toTicketResource(TicketEntity ticket) {
        return TicketResourceDto.builder()
                .id(ticket.getId())
                .seatName(ticket.getSeat().getRowChart() + ticket.getSeat().getSeatNumber())
                .auditoriumName(ticket.getSeat().getAuditorium().getName())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .build();
    }
}
