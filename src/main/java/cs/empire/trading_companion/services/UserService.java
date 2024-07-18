package cs.empire.trading_companion.services;

import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.entities.UserEntity;
import cs.empire.trading_companion.mappers.UserMapper;
import cs.empire.trading_companion.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO findUserByUsernameAndPassword(String username, String password) {
        UserEntity user = userRepository.findByUsernameAndPassword(username, password);
        if (user == null) {
            throw new RuntimeException("Invalid Username or Password");
        }
        return userMapper.userEntityToUserDto(user);
    }
}
