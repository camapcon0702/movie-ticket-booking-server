package qnt.moviebooking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.ShowtimeBulkRequestDto;
import qnt.moviebooking.dto.request.ShowtimeRequestDto;
import qnt.moviebooking.dto.resource.SeatAvailabilityDto;
import qnt.moviebooking.dto.resource.SeatResourceDto;
import qnt.moviebooking.dto.resource.ShowtimeResourceDto;
import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.entity.MovieEntity;
import qnt.moviebooking.entity.ShowtimeEntity;
import qnt.moviebooking.enums.MovieEnums;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.SeatRepository;
import qnt.moviebooking.repository.ShowtimeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieService movieService;
    private final AuditoriumService auditoriumService;
    private final SeatRepository seatRepository;
    @Transactional
    public List<ShowtimeResourceDto> createBulkShowtime(ShowtimeBulkRequestDto dto) {
        MovieEntity movie = movieService.getMovieEntityById(dto.getMovieId());
        AuditoriumEntity auditorium = auditoriumService.getAuditoriumEntityById(dto.getAuditoriumId());

        if (!movie.getStatus().equals(MovieEnums.NOW_SHOWING)) {
            throw new NotFoundException("Phim phải ở trạng thái NOW_SHOWING");
        }

        Set<LocalDateTime> uniqueTimes = new HashSet<>(dto.getStartTimes());
        if (uniqueTimes.size() != dto.getStartTimes().size()) {
            throw new NotFoundException("Danh sách startTime bị trùng");
        }

        List<ShowtimeEntity> showtimes = new ArrayList<>();

        for (LocalDateTime startTime : dto.getStartTimes()) {
            validateShowtimeConflict(auditorium, startTime, null);

            ShowtimeEntity showtime = ShowtimeEntity.builder()
                    .movie(movie)
                    .auditorium(auditorium)
                    .startTime(startTime)
                    .basePrice(dto.getBasePrice())
                    .build();

            showtimes.add(showtime);
        }

        showtimeRepository.saveAll(showtimes);

        return showtimes.stream()
                .map(this::mapToResource)
                .toList();
    }

    public List<ShowtimeResourceDto> getAllShowtime() {
        List<ShowtimeEntity> showtime = showtimeRepository.findAllByDeletedAtIsNull();
        return showtime.stream().map(this::mapToResource).toList();
    }



    public ShowtimeEntity getShowtimeEntityById(Long showtimeId) {
        return showtimeRepository.findByIdAndDeletedAtIsNull(showtimeId)
                .orElseThrow(() -> new NotFoundException("Không thấy suất chiếu với id: " + showtimeId));
    }

    public ShowtimeResourceDto getShowtimeById(Long showtimeId) {
        ShowtimeEntity showtime = showtimeRepository.findByIdAndDeletedAtIsNull(showtimeId)
                .orElseThrow(() -> new NotFoundException("Không thấy suất chiếu với id: " + showtimeId));
        return mapToResource(showtime);
    }

    public List<ShowtimeResourceDto> getShowtimeByMovie(Long movieId) {
        List<ShowtimeEntity> showtime = showtimeRepository.findAllByMovieIdAndDeletedAtIsNull(movieId);

        return showtime.stream().map(this::mapToResource).toList();
    }

    public List<ShowtimeResourceDto> getShowtimeByAuditorium(Long auditoriumId) {
        List<ShowtimeEntity> showtime = showtimeRepository.findAllByAuditoriumIdAndDeletedAtIsNull(auditoriumId);

        return showtime.stream().map(this::mapToResource).toList();
    }

    public List<SeatAvailabilityDto> getSeatByShowtime(Long showtimeId) {

        List<SeatAvailabilityDto> seats = seatRepository.findAvailableSeatsForShowtime(showtimeId);

        return seats;
    }


    @Transactional
    public ShowtimeResourceDto updateShowtime(Long showtimeId, ShowtimeRequestDto dto) {
        ShowtimeEntity showtime = getShowtimeEntityById(showtimeId);

        boolean movieChanged = !dto.getMovieId().equals(showtime.getMovie().getId());
        boolean auditoriumChanged = !dto.getAuditoriumId().equals(showtime.getAuditorium().getId());
        boolean startTimeChanged = !dto.getStartTime().equals(showtime.getStartTime());

        if (movieChanged) {
            MovieEntity movie = movieService.getMovieEntityById(dto.getMovieId());
            showtime.setMovie(movie);
        }

        if (auditoriumChanged || startTimeChanged) {
            AuditoriumEntity auditorium = auditoriumService.getAuditoriumEntityById(dto.getAuditoriumId());

            validateShowtimeConflict(auditorium, dto.getStartTime(), showtimeId);

            showtime.setAuditorium(auditorium);
            showtime.setStartTime(dto.getStartTime());
        }

        showtime.setBasePrice(dto.getBasePrice());

        return mapToResource(showtime);
    }

    @Transactional
    public void deletedShowtime(Long showtimeId) {
        ShowtimeEntity showtime = getShowtimeEntityById(showtimeId);

        showtime.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public void rollBackDeletedShowtime() {
        List<ShowtimeEntity> deletedShowtime = showtimeRepository
                .findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (deletedShowtime.isEmpty()) {
            throw new NotFoundException("Không có showtime nào để khôi phục!");
        }

        for (ShowtimeEntity genre : deletedShowtime) {
            genre.setDeletedAt(null);
        }

        showtimeRepository.saveAll(deletedShowtime);
    }

    private ShowtimeResourceDto mapToResource(ShowtimeEntity entity) {
        return ShowtimeResourceDto.builder()
                .id(entity.getId())
                .movieId(entity.getMovie().getId())
                .auditoriumId(entity.getAuditorium().getId())
                .basePrice(entity.getBasePrice())
                .startTime(entity.getStartTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private void validateShowtimeConflict(AuditoriumEntity auditorium, LocalDateTime startTime, Long showtimeId) {
        showtimeRepository
                .findByAuditoriumAndStartTimeAndDeletedAtIsNull(auditorium, startTime)
                .ifPresent(existing -> {
                    if (showtimeId == null
                            || !existing.getId().equals(showtimeId)) {
                        throw new NotFoundException(
                                "Trùng thời gian với suất chiếu có Id: " + existing.getId());
                    }
                });
    }

}
