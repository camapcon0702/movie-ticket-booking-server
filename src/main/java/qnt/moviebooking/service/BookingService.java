package qnt.moviebooking.service;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qnt.moviebooking.dto.request.BookingRequestDto;
import qnt.moviebooking.dto.request.OrderFoodRequestDto;
import qnt.moviebooking.dto.resource.BookingResourceDto;
import qnt.moviebooking.dto.resource.OrderFoodResourceDto;
import qnt.moviebooking.dto.resource.TicketResourceDto;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.entity.BookingFoodEntity;
import qnt.moviebooking.entity.FoodEntity;
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
    private final FoodService foodService;
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

        if (dto.getOrders() != null) {
            for (OrderFoodRequestDto orderDto : dto.getOrders()) {
                FoodEntity food = foodService.getFoodEntityById(orderDto.getFoodId());

                BookingFoodEntity bookingFood = BookingFoodEntity.builder()
                        .booking(booking)
                        .food(food)
                        .quantity(orderDto.getQuantity())
                        .build();

                booking.getBookingFoods().add(bookingFood);

                BigDecimal foodTotal = food.getPrice()
                        .multiply(BigDecimal.valueOf(orderDto.getQuantity()));
                total = total.add(foodTotal);
            }
        }

        if (voucher != null) {
            total = voucherService.applyVoucher(voucher, total);
            booking.setVoucher(voucher);
        }

        booking.setTotalAmount(total);

        bookingRepository.save(booking);

        return mapToResource(booking);
    }

    @Transactional
    public BookingResourceDto updateBookingStatus(Long bookingId, BookingEnums status) {
        BookingEntity booking = getBookingEntityById(bookingId);

        booking.setStatus(status);
        bookingRepository.save(booking);

        return mapToResource(booking);
    }

    public BookingResourceDto getBookingById(Long bookingId) {
        BookingEntity booking = getBookingEntityById(bookingId);

        return mapToResource(booking);
    }

    public BookingEntity getBookingEntityById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("Đặt chỗ không tồn tại"));
    }

    public List<BookingResourceDto> getAllBookings() {
        List<BookingEntity> bookings = bookingRepository.findAll();

        return bookings.stream()
                .map(this::mapToResource)
                .toList();
    }


    public List<BookingResourceDto> getBookingsByUser() {
        UserEntity user = userService.getCurrentUser();
        List<BookingEntity> bookings = bookingRepository.findByUserId(user.getId());

        return bookings.stream()
                .map(this::mapToResource)
                .toList();
    }

    @Transactional
    public void cancel(BookingEntity booking) {

        if (booking.getStatus() != BookingEnums.PENDING) {
            return;
        }

        booking.setStatus(BookingEnums.CANCELLED);

        bookingRepository.save(booking);
    }

    private BookingResourceDto mapToResource(BookingEntity booking) {
        return BookingResourceDto.builder()
                .id(booking.getId())
                .total(booking.getTotalAmount())
                .status(booking.getStatus())
                .startTime(booking.getShowtime().getStartTime())
                .tickets(booking.getTickets().stream().map(this::toTicketResource).toList())
                .orderedFoods(booking.getBookingFoods().stream().map(this::toOrderFoodResource).toList())
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

    private OrderFoodResourceDto toOrderFoodResource(BookingFoodEntity bookingFood) {
        return OrderFoodResourceDto.builder()
                .foodId(bookingFood.getFood().getId())
                .foodName(bookingFood.getFood().getName())
                .quantity(bookingFood.getQuantity())
                .build();
    }
}
