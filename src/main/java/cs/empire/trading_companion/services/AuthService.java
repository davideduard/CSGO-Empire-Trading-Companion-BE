package cs.empire.trading_companion.services;

import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.entities.UserEntity;
import cs.empire.trading_companion.mappers.UserMapper;
import cs.empire.trading_companion.repositories.AuthRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO findUserByUsername(String username) {
        Optional<UserEntity> user = authRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("Invalid Username or Password");
        }
        return userMapper.userEntityToUserDto(user.get());
    }

    public UserDTO saveUser(UserDTO userDTO) {
        UserEntity user = userMapper.userDtoToUserEntity(userDTO);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        UserEntity savedUser = authRepository.save(user);
        return userMapper.userEntityToUserDto(savedUser);
    }
}
