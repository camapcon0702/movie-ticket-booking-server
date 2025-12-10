package qnt.moviebooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qnt.moviebooking.dto.request.Booking.BookingRequestDto;
import qnt.moviebooking.dto.request.Booking.BookingUpdateRequestDto;
import qnt.moviebooking.dto.resource.Booking.BookingDetailResourceDto;
import qnt.moviebooking.dto.resource.Booking.BookingResourceDto;
import qnt.moviebooking.dto.resource.ServiceResourceDto;
import qnt.moviebooking.dto.resource.TicketResourceDto;
import qnt.moviebooking.dto.resource.VoucherResourceDto;
import qnt.moviebooking.entity.*;
import qnt.moviebooking.enums.BookingEnums;
import qnt.moviebooking.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final VoucherRepository voucherRepository;
    private final ShowtimeRepository showtimeRepository;
    private final TicketRepository ticketRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final BookingServiceRepository bookingServiceRepository;
    public BookingResourceDto createBooking(BookingRequestDto bookingRequestDto) {

        BigDecimal total = BigDecimal.ZERO;
        ShowtimeEntity showtime = showtimeRepository.findById(bookingRequestDto.getShowtime_id())
                .orElseThrow(() -> new RuntimeException("Không thấy suất chiếu"));
        List<TicketEntity> ticketEntityList = ticketRepository.findBookedSeatsByShowtime(showtime.getId());
        Set<Long> bookedSeatIds = ticketEntityList.stream()
                .map(ticketEntity -> ticketEntity.getSeat().getId())
                .collect(Collectors.toSet());
        List<Long> seatIds = Arrays.asList(bookingRequestDto.getSeatids());
        boolean hasConflict = seatIds.stream().anyMatch(bookedSeatIds::contains);
        if (hasConflict) {
            throw new IllegalArgumentException("Có ghế đã được đặt trước đó trong suất chiếu này!");
        }
        List<SeatEntity> seatEntities = seatRepository.findAllById(seatIds);
        if (seatIds.size() != seatEntities.size()) {
            throw new IllegalArgumentException("Một hoặc nhiều ghế không tồn tại!");
        }

        UserEntity user = userRepository.findById(bookingRequestDto.getUser_id())
                .orElseThrow(() -> new RuntimeException("Không thấy user"));

        VoucherEntity voucher = null;
        if (bookingRequestDto.getVoucher_id() != null) {
            voucher = voucherRepository.findById(bookingRequestDto.getVoucher_id())
                    .orElseThrow(() -> new RuntimeException("Không thấy voucher"));
        }

        List<ServiceEntity> serviceEntities = new ArrayList<>();

        if (bookingRequestDto.getServiceids() != null &&
                bookingRequestDto.getServiceids().length > 0) {
            List<Long> serviceIds = Arrays.asList(bookingRequestDto.getServiceids());
            serviceEntities = serviceRepository.findAllById(serviceIds);
            if (serviceIds.size() != serviceEntities.size()) {
                throw new IllegalArgumentException("Một hoặc nhiều dịch vụ không tồn tại!");
            }
        }

        BookingEntity booking = mapToEntity(user, voucher, showtime, total);
        booking = bookingRepository.save(booking);


        List<TicketEntity> listTicket = new ArrayList<>();
        for (SeatEntity seatEntity : seatEntities) {
            total = total.add(seatEntity.getPrice());
            String seatName = seatEntity.getRowChart() + seatEntity.getSeatNumber();
            String audiName = seatEntity.getAuditorium().getName();
            listTicket.add(
                    createTiket(
                            booking,
                            seatEntity,
                            seatName,
                            audiName
                    )
            );
        }
        ticketRepository.saveAll(listTicket);

        List<BookingServiceEntity> bookingServices = new ArrayList<>();
        for (ServiceEntity serviceEntity : serviceEntities) {
            total = total.add(serviceEntity.getPrice());
            bookingServices.add(
                    createBookingService(booking, serviceEntity)
            );
        }
        bookingServiceRepository.saveAll(bookingServices);

        if ((voucher != null )&&
                (voucher.getExpiry_date().isAfter(LocalDateTime.now())
                        || voucher.getExpiry_date().isEqual(LocalDateTime.now())))
        {
            if (voucher.getDiscount_percentage() != null) {
                Double percent = voucher.getDiscount_percentage();
                BigDecimal percentBD = BigDecimal.valueOf(percent);
                BigDecimal discount = total
                        .multiply(percentBD)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                total = total.subtract(discount);
            } else if (voucher.getDiscount_amount() != null) {
                total = total.subtract(voucher.getDiscount_amount());
                if (total.compareTo(BigDecimal.ZERO) < 0) {
                    total = BigDecimal.ZERO;
                }
            }
        }

        booking.setTotal_amount(total);

        bookingRepository.save(booking);

        return mapToDto(booking);
    }

    public BookingResourceDto updateBooking(Long bookingId, BookingUpdateRequestDto dto) {

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException ("Không tìm thấy booking với id: " + bookingId));

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (dto.getSeatids() != null && dto.getSeatids().length > 0) {
            Set<Long> requestedSeatIds = new HashSet<>(Arrays.asList(dto.getSeatids()));
            List<TicketEntity> existingTickets = ticketRepository
                    .findAllByBookingIdAndDeletedAtIsNull(bookingId);

            Set<Long> existingSeatIds = existingTickets.stream()
                    .map(t -> t.getSeat().getId())
                    .collect(Collectors.toSet());

            List<TicketEntity> ticketsToUpdate = new ArrayList<>();

            for (TicketEntity t : existingTickets) {
                Long seatId = t.getSeat().getId();

                if (!requestedSeatIds.contains(seatId)) {
                    t.setDeletedAt(LocalDateTime.now());
                } else {
                    totalAmount =totalAmount.add(t.getPrice());
                }
                ticketsToUpdate.add(t);
            }

            Set<Long> newSeatIds = new HashSet<>(requestedSeatIds);
            newSeatIds.removeAll(existingSeatIds);

            if (!newSeatIds.isEmpty()) {
                List<SeatEntity> newSeats = seatRepository.findAllById(newSeatIds);
                List<TicketEntity> newTickets = new ArrayList<>();

                for (SeatEntity seat : newSeats) {
                    String seatName = seat.getRowChart() + seat.getSeatNumber();
                    String audiName = seat.getAuditorium().getName();

                    TicketEntity ticket = createTiket(booking, seat, seatName, audiName);
                    newTickets.add(ticket);
                    totalAmount = totalAmount.add(ticket.getPrice());
                }
                ticketRepository.saveAll(newTickets);
            }

            if (!ticketsToUpdate.isEmpty()) {
                ticketRepository.saveAll(ticketsToUpdate);
            }
        }
        if (dto.getServiceids() != null && dto.getServiceids().length > 0) {
            Set<Long> requestedServiceIds = new HashSet<>(Arrays.asList(dto.getServiceids()));
            List<BookingServiceEntity> existingServices = bookingServiceRepository
                    .findAllByBookingIdAndDeletedAtIsNull(bookingId);

            List<BookingServiceEntity> servicesToUpdate = new ArrayList<>();

            for (BookingServiceEntity bs : existingServices) {
                Long serviceId = bs.getService().getId();

                if (!requestedServiceIds.contains(serviceId)) {
                    bs.setDeletedAt(LocalDateTime.now());
                } else {
                    totalAmount = totalAmount.add(bs.getService().getPrice());
                }
                servicesToUpdate.add(bs);
            }

            Set<Long> currentServiceIds = existingServices.stream()
                    .map(bs -> bs.getService().getId())
                    .collect(Collectors.toSet());

            Set<Long> newServiceIds = new HashSet<>(requestedServiceIds);
            newServiceIds.removeAll(currentServiceIds);

            if (!newServiceIds.isEmpty()) {
                List<ServiceEntity> newServices = serviceRepository
                        .findAllByIdInAndDeletedAtIsNull(newServiceIds);

                List<BookingServiceEntity> newBookingServices = new ArrayList<>();
                for (ServiceEntity service : newServices) {
                    BookingServiceEntity bs = createBookingService(booking, service);
                    newBookingServices.add(bs);
                    totalAmount = totalAmount.add(service.getPrice());
                }
                bookingServiceRepository.saveAll(newBookingServices);
            }

            if (!servicesToUpdate.isEmpty()) {
                bookingServiceRepository.saveAll(servicesToUpdate);
            }
        }

        VoucherEntity newVoucher = null;
        if (dto.getVoucher_id() != null) {
            newVoucher = voucherRepository.findById(dto.getVoucher_id())
                    .orElseThrow( () -> new RuntimeException(("Voucher không tồn tại"))) ;


            if (newVoucher.getExpiry_date() != null &&
                    newVoucher.getExpiry_date().isBefore(LocalDateTime.now())) {
                throw new RuntimeException(("Voucher đã hết hạn sử dụng"));
            }

            booking.setVoucher(newVoucher);

            if (newVoucher.getDiscount_percentage() != null && newVoucher.getDiscount_percentage() > 0) {
                BigDecimal percentBD = BigDecimal.valueOf(newVoucher.getDiscount_percentage());
                BigDecimal discount = totalAmount
                        .multiply(percentBD)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                totalAmount = totalAmount.subtract(discount);
            }
            else if (newVoucher.getDiscount_amount() != null) {
                BigDecimal amountDiscount = newVoucher.getDiscount_amount();

                totalAmount = totalAmount.subtract(amountDiscount);
            }
        } else {
            booking.setVoucher(null);
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) totalAmount = totalAmount.ZERO;


        booking.setTotal_amount(totalAmount);
        bookingRepository.save(booking);

        return mapToDto(booking);
    }

    public BookingResourceDto updateBookingAdmin(Long bookingId, BookingEnums newStatus) {

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        BookingEnums current = booking.getStatus();

        if (newStatus == null) {
            throw new IllegalArgumentException("Trạng thái cập nhật không hợp lệ!");
        }

        if (current == BookingEnums.ARCHIVED) {
            throw new IllegalArgumentException("Booking đã ARCHIVED, không thể cập nhật!");
        }

        switch (current) {
            case PENDING:
                if (newStatus != BookingEnums.PAID &&
                        newStatus != BookingEnums.CANCELLED) {
                    throw new IllegalArgumentException("PENDING chỉ đổi được sang PAID hoặc CANCELLED");
                }
                break;

            case PAID:
                if (newStatus != BookingEnums.SUCCESS &&
                        newStatus != BookingEnums.CANCELLED) {
                    throw new IllegalArgumentException("PAID chỉ đổi được sang SUCCESS hoặc CANCELLED");
                }
                break;

            case SUCCESS:
                if (newStatus != BookingEnums.ARCHIVED) {
                    throw new IllegalArgumentException("SUCCESS chỉ đổi được sang ARCHIVED");
                }
                break;

            case CANCELLED:
                if (newStatus != BookingEnums.ARCHIVED) {
                    throw new IllegalArgumentException("CANCELLED chỉ đổi được sang ARCHIVED");
                }
                break;
        }

        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        return mapToDto(booking);
    }

public List<BookingResourceDto> getAllBookings() {
    List<BookingEntity> listBookings = bookingRepository.findAllByDeletedAtIsNull();
    List<BookingResourceDto> bookingResources = new ArrayList<>();
    for (BookingEntity bookingEntity : listBookings) {
        bookingResources.add(mapToDto(bookingEntity));
    }

    return bookingResources;
}

public List<BookingResourceDto> getAllBookingsByUserId(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));
        List<BookingEntity> bookingServiceEntities = bookingRepository.findAllByUserIdAndDeletedAtIsNull(userId);
        List<BookingResourceDto> bookingResources = bookingServiceEntities.stream().map(this::mapToDto).toList();
        return bookingResources;
}

public BookingDetailResourceDto  getBookingDetail(Long bookingId) {
    BookingEntity bookingEntity = bookingRepository.findByIdAndDeletedAtIsNull(bookingId)
            .orElseThrow(() -> new RuntimeException("Không thấy booking"));
    List<TicketEntity> tickets = ticketRepository.findAllByBookingIdAndDeletedAtIsNull(bookingId);
    List<BookingServiceEntity> bookingServices = bookingServiceRepository.findAllByBookingIdAndDeletedAtIsNull(bookingId);
    List<Long> idServices = new ArrayList<>();

    for(BookingServiceEntity bookingServiceEntity : bookingServices) {
        idServices.add(bookingServiceEntity.getService().getId());
    }
    List<ServiceEntity> services =serviceRepository.findAllByIdInAndDeletedAtIsNull(idServices);
    VoucherResourceDto voucherResourceDto = mapToDtoVoucher(bookingEntity.getVoucher());
    List<TicketResourceDto> ticketResources = tickets.stream().map(this::mapToDtoTicket).toList();
    List<ServiceResourceDto> serviceResources = services.stream().map(this::mapToDtoService).toList();

    return mapToDtoDetail(bookingEntity,ticketResources,serviceResources,voucherResourceDto);
}


public void softDeleteBooking(Long bookingId) {
       BookingEntity bookingEntity = bookingRepository.findById(bookingId)
               .orElseThrow(()-> new RuntimeException("Booking not found"));
       bookingEntity.setDeletedAt(LocalDateTime.now());
       bookingRepository.save(bookingEntity);
}

public void rollbackDeleteBooking()
{
    List<BookingEntity> listBookings = bookingRepository.findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));
    if(listBookings.isEmpty())
    {
        throw new IllegalArgumentException("Không có booking nào để khôi phục!");
    }

    for (BookingEntity bookingEntity : listBookings) {
        bookingEntity.setDeletedAt(null);
    }

    bookingRepository.saveAll(listBookings);
}

                             /* ===================== COMMON ===================== */


    public TicketEntity createTiket (BookingEntity booking,SeatEntity seat,String seatName ,String audiName)
    {
        return TicketEntity.builder()
                .booking(booking)
                .seat(seat)
                .price(seat.getPrice())
                .audi_name(audiName)
                .seat_name(seatName)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public BookingServiceEntity createBookingService(BookingEntity bookingEntity,ServiceEntity serviceEntity){
        return BookingServiceEntity.builder()
                .booking(bookingEntity)
                .service(serviceEntity)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public  BookingEntity mapToEntity(UserEntity userEntity,VoucherEntity voucherEntity,ShowtimeEntity showtimeEntity,BigDecimal amount){
        return BookingEntity.builder()
                .bookingTime(LocalDateTime.now())
                .total_amount(amount)
                .status(BookingEnums.PENDING)
                .createdAt(LocalDateTime.now())
                .showtime(showtimeEntity)
                .user(userEntity)
                .voucher(voucherEntity)
                .build();
    }

    public BookingResourceDto mapToDto(BookingEntity entity) {

        return BookingResourceDto.builder()
                .id(entity.getId())
                .total(entity.getTotal_amount())
                .nameMovie(entity.getShowtime().getMovie().getTitle())
                .status(entity.getStatus())
                .showtime(entity.getShowtime().getStartTime())
                .startTimes(entity.getShowtime().getMovie().getReleaseDate())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .build();

    }

    public BookingResourceDto mapToDtoC(BookingEntity entity) {

        return BookingResourceDto.builder()
                .id(entity.getId())
                .total(entity.getTotal_amount())
                .nameMovie(entity.getShowtime().getMovie().getTitle())
                .status(entity.getStatus())
                .showtime(entity.getShowtime().getStartTime())
                .startTimes(entity.getShowtime().getMovie().getReleaseDate())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .build();

    }

    public BookingDetailResourceDto mapToDtoDetail(BookingEntity entity , List<TicketResourceDto> tickes , List<ServiceResourceDto> services ,VoucherResourceDto voucherResourceDto) {

        return BookingDetailResourceDto.builder()
                .Id(entity.getId())
                .total(entity.getTotal_amount())
                .bookingTime(entity.getCreatedAt())
                .userId(entity.getUser().getId())
                .FullName(entity.getUser().getFullName())
                .Email(entity.getUser().getEmail())
                .nameMovie(entity.getShowtime().getMovie().getTitle())
                .urlMovie(entity.getShowtime().getMovie().getPosterUrl())
                .status(entity.getStatus())
                .showtimeMovie(entity.getShowtime().getStartTime())
                .tickets(tickes)
                .services(services)
                .voucher(voucherResourceDto)
                .build();

    }

    public TicketResourceDto mapToDtoTicket(TicketEntity entity) {
        return TicketResourceDto.builder()
                .id(entity.getId())
                .audi_name(entity.getAudi_name())
                .seat_name(entity.getSeat_name())
                .price(entity.getPrice())
                .build();
    }

    public VoucherResourceDto mapToDtoVoucher(VoucherEntity entity) {
        return VoucherResourceDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discount_amount(entity.getDiscount_amount())
                .discount_percentage(entity.getDiscount_percentage())
                .expiry_date(entity.getExpiry_date())
                .build();
    }



    public ServiceResourceDto mapToDtoService(ServiceEntity entity) {
        return ServiceResourceDto.builder()
                .id(entity.getId())
                .urlimg(entity.getUrlimg())
                .name(entity.getName())
                .price(entity.getPrice())
                .build();
    }








}
