package qnt.moviebooking.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qnt.moviebooking.dto.ApiResponse;
import qnt.moviebooking.dto.resource.TicketResourceDto;
import qnt.moviebooking.entity.UserEntity;
import qnt.moviebooking.service.TicketService;
import qnt.moviebooking.service.UserService;

import java.util.List;

@RestController("ClientTicketController")
@RequiredArgsConstructor
@RequestMapping("/v1.0/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<TicketResourceDto>>> getTicketByUser() {
        UserEntity currentUser = userService.getCurrentUser();
        List<TicketResourceDto> tickets = ticketService.getTicketByUserId(currentUser.getId());

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách vé của người dùng thành công", tickets));
    }
}
