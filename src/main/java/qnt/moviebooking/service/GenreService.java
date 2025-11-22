package qnt.moviebooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.GenreRequestDto;
import qnt.moviebooking.dto.resource.GenreResourceDto;
import qnt.moviebooking.entity.GenreEntity;
import qnt.moviebooking.repository.GenreRepository;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreResourceDto createGenre(GenreRequestDto genreRequestDto) {
        if (genreRepository.existsByNameAndDeletedAtIsNull(genreRequestDto.getName())) {
            throw new IllegalArgumentException("Thể loại phim đã tồn tại!");
        }

        GenreEntity genreEntity = mapToEntity(genreRequestDto);
        GenreEntity savedEntity = genreRepository.save(genreEntity);

        return mapToDto(savedEntity);
    }

    public List<GenreResourceDto> getAllGenres() {
        List<GenreEntity> genreEntities = genreRepository.findAllByDeletedAtIsNull();

        return genreEntities.stream()
                .map(this::mapToDto)
                .toList();
    }

    public GenreResourceDto getGenreById(Long id) {
        GenreEntity genreEntity = genreRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim với ID: " + id));

        return mapToDto(genreEntity);
    }

    public GenreResourceDto getGenreByName(String name) {
        GenreEntity genreEntity = genreRepository.findByNameAndDeletedAtIsNull(name)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim với tên: " + name));

        return mapToDto(genreEntity);
    }

    public GenreResourceDto updateGenre(Long id, GenreRequestDto genreRequestDto) {
        GenreEntity genreEntity = genreRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim với ID: " + id));

        if (!genreEntity.getName().equals(genreRequestDto.getName()) &&
                genreRepository.existsByNameAndDeletedAtIsNull(genreRequestDto.getName())) {
            throw new IllegalArgumentException("Thể loại phim đã tồn tại!");
        }

        genreEntity.setName(genreRequestDto.getName());
        genreEntity.setDescription(genreRequestDto.getDescription());

        GenreEntity updatedEntity = genreRepository.save(genreEntity);

        return mapToDto(updatedEntity);
    }

    public void deleteGenre(Long id) {
        GenreEntity genreEntity = genreRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim với ID: " + id));

        genreEntity.setDeletedAt(LocalDateTime.now());
        genreRepository.save(genreEntity);
    }

    public void rollBackDeletedGenres() {
        List<GenreEntity> deletedGenres = genreRepository
                .findAllByDeletedAtBefore(LocalDateTime.now().minusMinutes(10));
        if (deletedGenres.isEmpty()) {
            throw new IllegalArgumentException("Không có thể loại phim nào để khôi phục!");
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

    private GenreResourceDto mapToDto(GenreEntity entity) {
        return GenreResourceDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt().toString())
                .updatedAt(entity.getUpdatedAt().toString())
                .build();
    }
}
