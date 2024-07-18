package cs.empire.trading_companion.mappers;

import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity userDtoToUserEntity(UserDTO userDTO);
    UserDTO userEntityToUserDto(UserEntity userEntity);
}
