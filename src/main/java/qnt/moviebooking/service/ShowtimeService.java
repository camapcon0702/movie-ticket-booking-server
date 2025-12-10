package qnt.moviebooking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.ShowtimeRequestDto;
import qnt.moviebooking.dto.resource.ShowtimeResourceDto;
import qnt.moviebooking.entity.AuditoriumEntity;
import qnt.moviebooking.entity.MovieEntity;
import qnt.moviebooking.entity.ShowtimeEntity;
import qnt.moviebooking.repository.AuditoriumRepository;
import qnt.moviebooking.repository.MovieRepository;
import qnt.moviebooking.repository.ShowtimeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final MovieRepository movieRepository;

    @Transactional
    public List<ShowtimeResourceDto> createShowtimes(ShowtimeRequestDto showtimeRequestDto) {
        MovieEntity movie = movieRepository.findByIdAndDeletedAtIsNull(showtimeRequestDto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Không thấy movie"));

        AuditoriumEntity auditorium = auditoriumRepository
                .findByIdAndDeletedAtIsNull(showtimeRequestDto.getAuditoriumId())
                .orElseThrow(() -> new RuntimeException("Không thấy Auditorium"));

        validateShowtimeConflicts(showtimeRequestDto.getStartTimes(), auditorium.getId());

        List<ShowtimeEntity> showtimes = mapToEnity(showtimeRequestDto, movie, auditorium);

        List<ShowtimeEntity> saved = showtimeRepository.saveAll(showtimes);

        return mapToGroupedDto(saved);
    }

    public List<ShowtimeResourceDto> selectShowtimeByMovie(Long movieId) {
        List<ShowtimeEntity> showtimes = showtimeRepository.findByMovieIdAndDeletedAtIsNotNull(movieId);

        if (showtimes.isEmpty()) {
            throw new RuntimeException("Không có suất  chiếu nào");
        }

        return mapToGroupedDto(showtimes);
    }

    public ShowtimeResourceDto selectShowtimeDetail(Long showtimeId) {
        ShowtimeEntity showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không thấy suất chiếu"));
        return mapToSingleDto(showtime);
    }

    @Transactional
    public ShowtimeResourceDto updateShowtime(Long showtimeId, ShowtimeRequestDto dto) {
        ShowtimeEntity existingShowtime = showtimeRepository.findByIdAndDeletedAtIsNull(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        MovieEntity movie = movieRepository.findByIdAndDeletedAtIsNull(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Không thấy movie"));

        AuditoriumEntity auditorium = auditoriumRepository.findByIdAndDeletedAtIsNull(dto.getAuditoriumId())
                .orElseThrow(() -> new RuntimeException("Không thấy Auditorium"));

        existingShowtime.setMovie(movie);
        existingShowtime.setAuditorium(auditorium);
        existingShowtime.setBasePrice(dto.getBasePrice());

        validateShowtimeConflicts(dto.getStartTimes(), auditorium.getId());

        if (dto.getStartTimes() != null && !dto.getStartTimes().isEmpty()) {
            existingShowtime.setStartTime(dto.getStartTimes().get(0));
        }

        ShowtimeEntity updated = showtimeRepository.save(existingShowtime);

        return mapToSingleDto(updated);

    }

    @Transactional
    public void softDeleteShowtime(Long showtimeId) {
        ShowtimeEntity showtime = showtimeRepository.findByIdAndDeletedAtIsNull(showtimeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xuất chiếu"));

        showtime.setDeletedAt(LocalDateTime.now());

        showtimeRepository.save(showtime);

    }

    @Transactional
    public void rollBackDeletedShowtimes() {
        List<ShowtimeEntity> deletedShowtime = showtimeRepository
                .findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (showtimeRepository.count() == 0) {
            throw new IllegalArgumentException("Không có showtime nào để khôi phục!");
        }

        for (ShowtimeEntity genre : deletedShowtime) {
            genre.setDeletedAt(null);
        }

        showtimeRepository.saveAll(deletedShowtime);
    }

    private List<ShowtimeEntity> mapToEnity(ShowtimeRequestDto Dto, MovieEntity movie, AuditoriumEntity auditorium) {
        return Dto.getStartTimes().stream()
                .map(time -> ShowtimeEntity.builder()
                        .startTime(time)
                        .basePrice(Dto.getBasePrice())
                        .movie(movie)
                        .auditorium(auditorium)
                        .build())
                .toList();
    }

    private List<ShowtimeResourceDto> mapToGroupedDto(List<ShowtimeEntity> entities) {
        return entities.stream()
                .map(entity -> ShowtimeResourceDto.builder()
                        .id(entity.getId())
                        .movieId(entity.getMovie().getId())
                        .auditoriumId(entity.getAuditorium().getId())
                        .basePrice(entity.getBasePrice())
                        .startTimes(entity.getStartTime())
                        .createdAt(entity.getCreatedAt())
                        .updatedAt(entity.getUpdatedAt())
                        .build())
                .toList();

    }

    private ShowtimeResourceDto mapToSingleDto(ShowtimeEntity entity) {
        return ShowtimeResourceDto.builder()
                .movieId(entity.getMovie().getId())
                .auditoriumId(entity.getAuditorium().getId())
                .basePrice(entity.getBasePrice())
                .startTimes(entity.getStartTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private void validateShowtimeConflicts(List<LocalDateTime> startTimes, Long auditoriumId) {

        List<ShowtimeEntity> conflicts = showtimeRepository
                .findByAuditoriumIdAndStartTimeIn(auditoriumId, startTimes);
        if (!conflicts.isEmpty()) {
            String conflictsTime = conflicts.stream()
                    .map(s -> s.getStartTime().toString())
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Thời gian bị trùng với phim khác" + conflictsTime);
        }
    }
}
