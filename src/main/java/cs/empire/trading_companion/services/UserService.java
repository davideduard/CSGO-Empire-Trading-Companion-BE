package cs.empire.trading_companion.services;

import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.entities.UserEntity;
import cs.empire.trading_companion.exceptions.UserNotFoundException;
import cs.empire.trading_companion.mappers.UserMapper;
import cs.empire.trading_companion.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO getUserByEmail(String email) {
        Optional<UserEntity> foundUser = this.userRepository.findByEmail(email);
        if (foundUser.isPresent()) {
            return this.userMapper.userEntityToUserDto(foundUser.get());
        }

        throw new UserNotFoundException("The specified user email doesn't exist");
    }
}
