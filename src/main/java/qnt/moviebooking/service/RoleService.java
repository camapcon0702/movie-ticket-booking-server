package qnt.moviebooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import qnt.moviebooking.dto.request.RoleRequestDto;
import qnt.moviebooking.dto.resource.RoleResourceDto;
import qnt.moviebooking.entity.RoleEntity;
import qnt.moviebooking.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    // Tạo mới role
    public RoleResourceDto createRole(RoleRequestDto request) {
        if (roleRepository.existsByRoleName(request.getName())) {
            throw new IllegalArgumentException("Role đã tồn tại: " + request.getName());
        }

        RoleEntity roleEntity = mapToEntity(request);
        RoleEntity savedEntity = roleRepository.save(roleEntity);

        return mapToDto(savedEntity);
    }

    // Lấy tất cả role
    public List<RoleResourceDto> getAllRoles() {
        List<RoleEntity> roles = roleRepository.findAll();

        return roles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Lấy role theo ID
    public RoleResourceDto getRoleById(Long id) {
        RoleEntity roleEntity = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại với id: " + id));

        return mapToDto(roleEntity);
    }

    // Cập nhật role
    public RoleResourceDto updateRole(Long id, RoleRequestDto request) {
        RoleEntity existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại với id: " + id));

        existingRole.setRoleName(request.getName());
        existingRole.setDescription(request.getDescription());

        RoleEntity updatedRole = roleRepository.save(existingRole);

        return mapToDto(updatedRole);
    }

    // Chuyển từ DTO(request) sang Entity
    private RoleEntity mapToEntity(RoleRequestDto dto) {
        return RoleEntity.builder()
                .roleName(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    // Chuyển từ Entity sang DTO(resource)
    private RoleResourceDto mapToDto(RoleEntity entity) {
        return RoleResourceDto.builder()
                .id(entity.getId())
                .name(entity.getRoleName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}