package qnt.moviebooking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.enums.TicketEnums;
import qnt.moviebooking.repository.TicketRepository;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    public boolean isSeatBooked(Long seatId, Long showtimeId) {
        return ticketRepository.existsBySeatIdAndBookingShowtimeIdAndStatusIn(
                seatId,
                showtimeId,
                List.of(TicketEnums.HELD, TicketEnums.CONFIRMED));
    }
}
