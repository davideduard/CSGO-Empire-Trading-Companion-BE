package cs.empire.trading_companion.services;

import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.entities.UserEntity;
import cs.empire.trading_companion.exceptions.InvalidFormatException;
import cs.empire.trading_companion.exceptions.UnauthorizedException;
import cs.empire.trading_companion.exceptions.UserNotFoundException;
import cs.empire.trading_companion.mappers.UserMapper;
import cs.empire.trading_companion.repositories.UserRepository;
import cs.empire.trading_companion.security.services.JWTService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JWTService tokenService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, JWTService tokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserByEmail(String email, String token) {
        String currentUser = tokenService.extractUsername(token);

        Optional<UserEntity> foundUser = this.userRepository.findByEmail(email);
        UserEntity user = null;

        if (foundUser.isPresent()) {
            user = foundUser.get();
        }

        if (user == null) {
            throw new UserNotFoundException("The specified user email doesn't exist");
        }

        if (user.getUsername().equals(currentUser)) {
            return this.userMapper.userEntityToUserDto(foundUser.get());
        }

        throw new UnauthorizedException("Insufficient Permission");
    }

    public UserDTO updateUser(String email, UserDTO newUserDTO, String token) {
        String currentUser = tokenService.extractUsername(token);

        Optional<UserEntity> foundUser = this.userRepository.findByEmail(email);
        UserEntity oldUser = null;

        if (foundUser.isPresent()) {
            oldUser = foundUser.get();
        }

        if (oldUser == null) {
            throw new UserNotFoundException("The specified user doesn't exist");
        }

        if (oldUser.getUsername().equals(currentUser)) {
            validateUserDetails(newUserDTO);

            oldUser.setEmail(newUserDTO.getEmail());
            oldUser.setFirstName(newUserDTO.getFirstName());
            oldUser.setLastName(newUserDTO.getLastName());
            oldUser.setEmpireToken(newUserDTO.getEmpireToken());

            if (newUserDTO.getPassword() != null) {
                String encodedPassword = passwordEncoder.encode(newUserDTO.getPassword());
                oldUser.setPassword(encodedPassword);
            }

            this.userRepository.save(oldUser);
            return this.userMapper.userEntityToUserDto(oldUser);
        }

        throw new UnauthorizedException("Insufficient Permission");
    }

    private void validateUserDetails(UserDTO userDTO) {
        String userEmail = userDTO.getEmail();
        String userPassword = userDTO.getPassword();

        if (userEmail != null && !userEmail.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidFormatException("Please use a valid email format.");
        }

        if (userPassword != null && !userPassword.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$")) {
            throw new InvalidFormatException("The password must be at least 8 characters long, contain at least 1 capital letter," +
                    " 1 number and 1 special character (!, @, #, $, %, ^, &, *, ., ?)");
        }
    }
}
