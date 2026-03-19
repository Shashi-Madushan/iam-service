package lk.ijse.eca.customerservice.mapper;

import lk.ijse.eca.customerservice.dto.UserRequestDTO;
import lk.ijse.eca.customerservice.dto.UserResponseDTO;
import lk.ijse.eca.customerservice.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    UserResponseDTO toDto(User user);

    User toEntity(UserRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UserRequestDTO dto, @MappingTarget User user);

    @Mapping(target = "password", ignore = true)
    UserResponseDTO toDtoWithoutPassword(User user);
}
