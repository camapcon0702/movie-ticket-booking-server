package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.GenreRequestDto;
import qnt.moviebooking.dto.resource.GenreResourceDto;
import qnt.moviebooking.entity.GenreEntity;
import qnt.moviebooking.exception.ExistException;
import qnt.moviebooking.exception.NotFoundException;
import qnt.moviebooking.repository.GenreRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {
    private final GenreRepository genreRepository;

    @Transactional
    public GenreResourceDto createGenre(GenreRequestDto genreRequestDto) {
        if (genreRepository.existsByNameAndDeletedAtIsNull(genreRequestDto.getName())) {
            throw new ExistException("Thể loại phim đã tồn tại!");
        }

        GenreEntity genreEntity = mapToEntity(genreRequestDto);
        GenreEntity savedEntity = genreRepository.save(genreEntity);

        return mapToResource(savedEntity);
    }

    public List<GenreResourceDto> getAllGenres() {
        List<GenreEntity> genreEntities = genreRepository.findAllByDeletedAtIsNull();

        return genreEntities.stream()
                .map(this::mapToResource)
                .toList();
    }

    public GenreEntity getGenreEntityById(Long id) {
        return genreRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thể loại phim với ID: " + id));
    }

    public GenreResourceDto getGenreById(Long id) {
        GenreEntity genreEntity = getGenreEntityById(id);

        return mapToResource(genreEntity);
    }

    public GenreResourceDto getGenreByName(String name) {
        GenreEntity genreEntity = genreRepository.findByNameAndDeletedAtIsNull(name)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thể loại phim với tên: " + name));

        return mapToResource(genreEntity);
    }

    @Transactional
    public GenreResourceDto updateGenre(Long id, GenreRequestDto genreRequestDto) {
        GenreEntity genreEntity = getGenreEntityById(id);

        if (!genreEntity.getName().equals(genreRequestDto.getName()) &&
                genreRepository.existsByNameAndDeletedAtIsNull(genreRequestDto.getName())) {
            throw new ExistException("Thể loại phim đã tồn tại!");
        }

        genreEntity.setName(genreRequestDto.getName());
        genreEntity.setDescription(genreRequestDto.getDescription());

        GenreEntity updatedEntity = genreRepository.save(genreEntity);

        return mapToResource(updatedEntity);
    }

    @Transactional
    public void deleteGenre(Long id) {
        GenreEntity genreEntity = getGenreEntityById(id);

        if (!genreEntity.getMovieGenres().isEmpty()) {
            throw new ValidationException(
                    "Không thể xóa thể loại đang được sử dụng");
        }

        genreEntity.setDeletedAt(LocalDateTime.now());
        genreRepository.save(genreEntity);
    }

    @Transactional
    public void rollBackDeletedGenres() {
        List<GenreEntity> deletedGenres = genreRepository
                .findAllByDeletedAtAfter(LocalDateTime.now().minusMinutes(10));

        if (deletedGenres.isEmpty()) {
            throw new NotFoundException("Không có thể loại phim nào để khôi phục!");
        }

        for (GenreEntity genre : deletedGenres) {
            genre.setDeletedAt(null);
        }

        genreRepository.saveAll(deletedGenres);
    }

    private GenreEntity mapToEntity(GenreRequestDto dto) {
        return GenreEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    private GenreResourceDto mapToResource(GenreEntity entity) {
        return GenreResourceDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt().toString())
                .updatedAt(entity.getUpdatedAt().toString())
                .build();
    }
}
