package qnt.moviebooking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.MovieRequestDto;
import qnt.moviebooking.dto.resource.MovieResourceDto;
import qnt.moviebooking.entity.GenreEntity;
import qnt.moviebooking.entity.MovieEntity;
import qnt.moviebooking.entity.MovieGenreEntity;
import qnt.moviebooking.enums.MovieEnums;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.MovieRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreService genreService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final List<MovieEnums> USER_VISIBLE_STATUSES = List.of(
            MovieEnums.NOW_SHOWING,
            MovieEnums.COMING_SOON);

    @Transactional
    public MovieResourceDto createMovie(MovieRequestDto dto) {
        validateTitle(dto.getTitle(), null);
        validateGenres(dto.getGenreIds());

        MovieEntity movie = MovieEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .releaseDate(LocalDate.parse(dto.getReleaseDate(), DATE_FORMATTER))
                .posterUrl(dto.getPosterUrl())
                .trailerUrl(dto.getTrailerUrl())
                .status(MovieEnums.valueOf(dto.getStatus().toUpperCase()))
                .starNumber(dto.getStarNumber())
                .build();
        attachGenres(movie, dto.getGenreIds());
        movieRepository.save(movie);

        return mapToResource(movie);
    }

    @Transactional
    public MovieResourceDto updateMovie(Long id, MovieRequestDto dto) {
        MovieEntity movie = getMovieEntityById(id);

        validateTitle(dto.getTitle(), movie.getId());
        validateGenres(dto.getGenreIds());

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDurationMinutes(dto.getDurationMinutes());
        movie.setReleaseDate(LocalDate.parse(dto.getReleaseDate(), DATE_FORMATTER));
        movie.setPosterUrl(dto.getPosterUrl());
        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setStatus(MovieEnums.valueOf(dto.getStatus().toUpperCase()));
        movie.setStarNumber(dto.getStarNumber());
        movie.getMovieGenres().clear();
        attachGenres(movie, dto.getGenreIds());

        return mapToResource(movieRepository.save(movie));
    }

    public MovieEntity getMovieEntityById(Long id) {
        return movieRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Phim không tồn tại với id: " + id));
    }

    @Transactional
    public void deleteMovie(Long id) {
        MovieEntity movie = getMovieEntityById(id);
        movie.setDeletedAt(LocalDateTime.now());
        movieRepository.save(movie);
    }

    @Transactional
    public void rollBackDeletedMovies() {
        List<MovieEntity> movies = movieRepository
                .findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));
        if (movies.isEmpty()) {
            throw new NotFoundException("Không có phim nào để khôi phục!");
        }

        movies.forEach(m -> m.setDeletedAt(null));

        movieRepository.saveAll(movies);
    }

    public List<MovieResourceDto> getAllMoviesAdmin() {
        return movieRepository.findAllByDeletedAtIsNull()
                .stream().map(this::mapToResource).toList();
    }

    public MovieResourceDto getMovieByIdAdmin(Long id) {
        MovieEntity movie = getMovieEntityById(id);
        return mapToResource(movie);
    }

    public List<MovieResourceDto> searchMoviesByTitleAdmin(String keyword) {
        return movieRepository.findByTitleContainingIgnoreCaseAndDeletedAtIsNull(keyword)
                .stream().map(this::mapToResource).toList();
    }

    public List<MovieResourceDto> getMoviesByGenreId(Long genreId) {
        return movieRepository.findDistinctByMovieGenresGenreIdAndDeletedAtIsNull(genreId)
                .stream().map(this::mapToResource).toList();
    }

    public List<MovieResourceDto> getAllMoviesForUser() {
        return movieRepository.findAllByStatusInAndDeletedAtIsNull(USER_VISIBLE_STATUSES)
                .stream().map(this::mapToResource).toList();
    }

    public List<MovieResourceDto> getMoviesByStatusForUser(MovieEnums status) {
        return movieRepository.findAllByStatusAndDeletedAtIsNull(status)
                .stream().map(this::mapToResource).toList();
    }

    public List<MovieResourceDto> searchMoviesByTitleForUser(String keyword) {
        return movieRepository
                .findByTitleContainingIgnoreCaseAndDeletedAtIsNullAndStatusIn(keyword, USER_VISIBLE_STATUSES)
                .stream().map(this::mapToResource).toList();
    }

    public MovieResourceDto getMovieByIdForUser(Long id) {
        MovieEntity movie = movieRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Phim không tồn tại với id: " + id));

        if (!USER_VISIBLE_STATUSES.contains(movie.getStatus())) {
            return null;
        }

        return mapToResource(movie);
    }

    public List<MovieResourceDto> getMoviesByGenreForUser(Long genreId) {
        List<MovieEntity> movies = movieRepository.findDistinctByMovieGenresGenreIdAndDeletedAtIsNull(genreId);

        List<MovieEntity> filtered = movies.stream()
                .filter(m -> USER_VISIBLE_STATUSES.contains(m.getStatus()))
                .toList();

        return filtered.stream()
                .map(this::mapToResource)
                .toList();
    }

    private void validateTitle(String title, Long excludeId) {
        boolean exists = (excludeId == null) ? movieRepository.existsByTitleAndDeletedAtIsNull(title)
                : movieRepository.findByTitleAndDeletedAtIsNull(title)
                        .filter(m -> !m.getId().equals(excludeId)).isPresent();
        if (exists) {
            throw new ExistException("Tên phim đã tồn tại: " + title);
        }
    }

    private void validateGenres(Long[] genreIds) {
        if (genreIds == null || genreIds.length == 0) {
            throw new NotFoundException("Phim phải có ít nhất 1 thể loại");
        }
        for (Long id : genreIds) {
            if (!genreService.getGenreEntityById(id).getId().equals(id)) {
                throw new NotFoundException("Thể loại không tồn tại: " + id);
            }
        }
    }

    private void attachGenres(MovieEntity movie, Long[] genreIds) {
        for (Long genreId : genreIds) {
            GenreEntity genre = genreService.getGenreEntityById(genreId);
            movie.getMovieGenres().add(
                    MovieGenreEntity.builder()
                            .movie(movie)
                            .genre(genre)
                            .build());
        }
    }

    public MovieResourceDto mapToResource(MovieEntity entity) {
        String[] genres = entity.getMovieGenres().stream()
                .map(mg -> mg.getGenre().getName())
                .toArray(String[]::new);

        return MovieResourceDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .durationMinutes(entity.getDurationMinutes())
                .releaseDate(entity.getReleaseDate().format(DATE_FORMATTER))
                .posterUrl(entity.getPosterUrl())
                .trailerUrl(entity.getTrailerUrl())
                .status(entity.getStatus().name())
                .starNumber(entity.getStarNumber())
                .genres(genres)
                .build();
    }
}
