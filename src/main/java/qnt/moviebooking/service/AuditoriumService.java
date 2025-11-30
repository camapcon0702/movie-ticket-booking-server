package qnt.moviebooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qnt.moviebooking.dto.resource.AuditoriumResourceDto;
import qnt.moviebooking.repository.AuditoriumRepository;


@Service
@RequiredArgsConstructor
public class AuditoriumService {
    private final AuditoriumRepository userRepository;


}
