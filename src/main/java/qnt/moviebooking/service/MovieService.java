package qnt.moviebooking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.MovieRequestDto;
import qnt.moviebooking.dto.resource.MovieResouresDto;
import qnt.moviebooking.entity.GenreEntity;
import qnt.moviebooking.entity.MovieEntity;
import qnt.moviebooking.enums.MovieEnums;
import qnt.moviebooking.repository.GenreRepository;
import qnt.moviebooking.repository.MovieRepository;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /* ===================== ADMIN ===================== */

    public MovieResouresDto createMovie(MovieRequestDto dto) {
        validateTitle(dto.getTitle(), null);
        validateGenres(dto.getGenreIds());
        MovieEntity movieEntity = mapToEntity(dto);
        return mapToDto(movieRepository.save(movieEntity));
    }

    public MovieResouresDto updateMovie(Long id, MovieRequestDto dto) {
        MovieEntity existingMovie = movieRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại với id: " + id));

        validateTitle(dto.getTitle(), existingMovie.getId());
        validateGenres(dto.getGenreIds());

        MovieEntity updatedMovie = mapToEntity(dto);
        updatedMovie.setId(existingMovie.getId());
        return mapToDto(movieRepository.save(updatedMovie));
    }

    public void deleteMovie(Long id) {
        MovieEntity movie = movieRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại với id: " + id));
        movie.setDeletedAt(LocalDateTime.now());
        movieRepository.save(movie);
    }

    public void rollBackDeletedMovies() {
        List<MovieEntity> deletedMovies = movieRepository
                .findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));
        if (deletedMovies.isEmpty()) {
            throw new IllegalArgumentException("Không có phim nào để khôi phục!");
        }

        for (MovieEntity movie : deletedMovies) {
            movie.setDeletedAt(null);
        }

        movieRepository.saveAll(deletedMovies);
    }

    public List<MovieResouresDto> getAllMoviesAdmin() {
        return movieRepository.findAllByDeletedAtIsNull()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public MovieResouresDto getMovieByIdAdmin(Long id) {
        MovieEntity movie = movieRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại với id: " + id));
        return mapToDto(movie);
    }

    public List<MovieResouresDto> searchMoviesByTitleAdmin(String keyword) {
        return movieRepository.findByTitleContainingIgnoreCaseAndDeletedAtIsNull(keyword)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<MovieResouresDto> getMoviesByGenreId(Long genreId) {
        return movieRepository.findDistinctByGenresIdAndDeletedAtIsNull(genreId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /* ===================== USER ===================== */

    private static final List<MovieEnums> USER_VISIBLE_STATUSES = Arrays.asList(
            MovieEnums.NOW_SHOWING,
            MovieEnums.COMING_SOON);

    public List<MovieResouresDto> getAllMoviesForUser() {
        return movieRepository.findAllByStatusInAndDeletedAtIsNull(USER_VISIBLE_STATUSES)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<MovieResouresDto> searchMoviesByTitleForUser(String keyword) {
        return movieRepository
                .findByTitleContainingIgnoreCaseAndDeletedAtIsNullAndStatusIn(keyword, USER_VISIBLE_STATUSES)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public MovieResouresDto getMovieByIdForUser(Long id) {
        MovieEntity movie = movieRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại với id: " + id));

        if (!USER_VISIBLE_STATUSES.contains(movie.getStatus())) {
            return null;
        }

        return mapToDto(movie);
    }

    public List<MovieResouresDto> getMoviesByGenreForUser(Long genreId) {
        List<MovieEntity> movies = movieRepository.findDistinctByGenresIdAndDeletedAtIsNull(genreId);

        List<MovieEntity> filtered = movies.stream()
                .filter(m -> USER_VISIBLE_STATUSES.contains(m.getStatus()))
                .toList();

        return filtered.stream()
                .map(this::mapToDto)
                .toList();
    }

    /* ===================== COMMON ===================== */

    private void validateTitle(String title, Long excludeId) {
        boolean exists = (excludeId == null) ? movieRepository.existsByTitleAndDeletedAtIsNull(title)
                : movieRepository.findByTitleAndDeletedAtIsNull(title)
                        .filter(m -> !m.getId().equals(excludeId)).isPresent();
        if (exists) {
            throw new IllegalArgumentException("Tên phim đã tồn tại: " + title);
        }
    }

    private void validateGenres(Long[] genreIds) {
        if (genreIds == null || genreIds.length == 0) {
            throw new IllegalArgumentException("Phim phải có ít nhất 1 thể loại");
        }
        for (Long id : genreIds) {
            if (!genreRepository.findByIdAndDeletedAtIsNull(id).isPresent()) {
                throw new IllegalArgumentException("Thể loại không tồn tại: " + id);
            }
        }
    }

    private MovieEntity mapToEntity(MovieRequestDto dto) {
        MovieEnums status;
        try {
            status = MovieEnums.valueOf(dto.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái không tồn tại: " + dto.getStatus());
        }

        List<GenreEntity> genres = Arrays.stream(dto.getGenreIds())
                .map(id -> genreRepository.findByIdAndDeletedAtIsNull(id).get())
                .collect(Collectors.toList());

        LocalDate releaseDate;
        try {
            releaseDate = LocalDate.parse(dto.getReleaseDate(), DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ngày phát hành không hợp lệ, đúng định dạng: dd-MM-yyyy");
        }

        return MovieEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .releaseDate(releaseDate)
                .posterUrl(dto.getPosterUrl())
                .trailerUrl(dto.getTrailerUrl())
                .status(status)
                .starNumber(dto.getStarNumber())
                .genres(genres)
                .build();
    }

    public MovieResouresDto mapToDto(MovieEntity entity) {
        String[] genreNames = entity.getGenres().stream()
                .map(GenreEntity::getName)
                .toArray(String[]::new);

        String releaseDate = entity.getReleaseDate() != null
                ? entity.getReleaseDate().format(DATE_FORMATTER)
                : null;

        return MovieResouresDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .durationMinutes(entity.getDurationMinutes())
                .releaseDate(releaseDate)
                .posterUrl(entity.getPosterUrl())
                .trailerUrl(entity.getTrailerUrl())
                .status(entity.getStatus().name())
                .starNumber(entity.getStarNumber())
                .genres(genreNames)
                .build();
    }
}
