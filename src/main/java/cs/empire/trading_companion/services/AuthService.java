package cs.empire.trading_companion.services;

import cs.empire.trading_companion.dtos.LoginRequestDTO;
import cs.empire.trading_companion.dtos.LoginResponseDTO;
import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.entities.UserEntity;
import cs.empire.trading_companion.exceptions.InvalidFormatException;
import cs.empire.trading_companion.exceptions.UserAlreadyExistsException;
import cs.empire.trading_companion.exceptions.UserNotFoundException;
import cs.empire.trading_companion.mappers.UserMapper;
import cs.empire.trading_companion.repositories.AuthRepository;
import cs.empire.trading_companion.security.services.JWTService;
import cs.empire.trading_companion.security.services.MyUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final MyUserDetailsService userDetailsService;

    public AuthService(AuthRepository authRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, MyUserDetailsService userDetailsService) {
        this.authRepository = authRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        Optional<UserEntity> foundUser = authRepository.findByUsername(loginRequestDTO.getUsername());

        if (foundUser.isPresent()) {
            String requestEncodedPassword = passwordEncoder.encode(loginRequestDTO.getPassword());

            if (requestEncodedPassword.equals(foundUser.get().getPassword())) {
                LoginResponseDTO loginResponse = new LoginResponseDTO();
                String token = jwtService.generateToken(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername()));
                loginResponse.setToken(token);
                return loginResponse;
            }
        }

        throw new UserNotFoundException("We couldn't find an user with those credentials. Please try again!");
    }

    public UserDTO saveUser(UserDTO userDTO) throws UserAlreadyExistsException{
        Optional<UserEntity> foundUserByUsername = authRepository.findByUsername(userDTO.getUsername());
        Optional<UserEntity> foundUserByEmail = authRepository.findByEmail(userDTO.getEmail());

        if (foundUserByEmail.isPresent()) {
            throw new UserAlreadyExistsException("An user with this email already exists.");
        }

        if (foundUserByUsername.isPresent()) {
            throw new UserAlreadyExistsException("An user with this username already exists.");
        }

        validateUserDetails(userDTO);

        UserEntity user = userMapper.userDtoToUserEntity(userDTO);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        UserEntity savedUser = authRepository.save(user);
        return userMapper.userEntityToUserDto(savedUser);
    }

    private void validateUserDetails(UserDTO userDTO) {
        String userUsername = userDTO.getUsername();
        String userEmail = userDTO.getEmail();
        String userPassword = userDTO.getPassword();

        if (!userUsername.matches("^[A-Za-z0-9._-]{3,}$")) {
            throw new InvalidFormatException("Username can only contain letters, numbers and allowed characters: -, _, .");
        }

        if (!userEmail.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidFormatException("Please use a valid email format.");
        }

        if (!userPassword.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$")) {
            throw new InvalidFormatException("The password must be atleast 8 characters long, contain at least 1 capital letter," +
                    " 1 number and 1 special character (!, @, #, $, %, ^, &, *, ., ?)");
        }
    }
}
