package qnt.moviebooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.resource.TicketResourceDto;
import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.entity.BookingEntity;
import qnt.moviebooking.entity.SeatEntity;
import qnt.moviebooking.entity.TicketEntity;
import qnt.moviebooking.enums.TicketEnums;
import qnt.moviebooking.repository.BookingRepository;
import qnt.moviebooking.repository.SeatRepository;
import qnt.moviebooking.repository.TicketRepository;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    public boolean isSeatBooked(Long seatId, Long showtimeId) {
        return ticketRepository.existsBySeatIdAndBookingShowtimeIdAndStatusIn(
                seatId,
                showtimeId,
                List.of(TicketEnums.HELD, TicketEnums.CONFIRMED)
        );
    }

    public List<TicketResourceDto> getTicketByUserId(Long userId) {

        List<BookingEntity> userBookings = bookingRepository.findByUserId(userId);

        if (userBookings.isEmpty()) {
            return List.of();
        }

        List<Long> bookingIds = userBookings.stream()
                .map(BookingEntity::getId)
                .toList();

        return ticketRepository.findByBookingIdIn(bookingIds)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private TicketResourceDto mapToDto(TicketEntity ticket) {

        SeatEntity seat = ticket.getSeat();               // dùng quan hệ JPA
        AuditoriumEntity auditorium = seat.getAuditorium();

        return TicketResourceDto.builder()
                .id(ticket.getId())
                .price(ticket.getPrice())
                .auditoriumName(auditorium.getName())
                .seatName(seat.getRowChart() + seat.getSeatNumber())
                .build();
    }
}
