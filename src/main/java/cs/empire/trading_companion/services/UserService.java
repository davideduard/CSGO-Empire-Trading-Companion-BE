package cs.empire.trading_companion.services;

import cs.empire.trading_companion.dtos.EmpireTokenDTO;
import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.entities.UserEntity;
import cs.empire.trading_companion.exceptions.InvalidFormatException;
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

    public UserDTO updateUser(UserDTO newUserDTO, String token) {
        String currentUser = tokenService.extractUsername(token);
        Optional<UserEntity> oldUser = userRepository.findByUsername(currentUser);
        UserEntity oldUserEntity = null;

        if (oldUser.isPresent()) {
            oldUserEntity = oldUser.get();
            validateUserDetails(newUserDTO);

            oldUserEntity.setEmail(newUserDTO.getEmail());
            oldUserEntity.setFirstName(newUserDTO.getFirstName());
            oldUserEntity.setLastName(newUserDTO.getLastName());

            if (newUserDTO.getPassword() != null) {
                String encodedPassword = passwordEncoder.encode(newUserDTO.getPassword());
                oldUserEntity.setPassword(encodedPassword);
            }

            this.userRepository.save(oldUserEntity);
            return this.userMapper.userEntityToUserDto(oldUserEntity);
        }

        throw new UserNotFoundException("User cannot be found");
    }

    public UserDTO setEmpireToken(EmpireTokenDTO empireToken, String authToken) {
        String currentUser = tokenService.extractUsername(authToken);
        Optional<UserEntity> userToModify = userRepository.findByUsername(currentUser);
        UserEntity userEntity = null;

        if (userToModify.isPresent()) {
            userEntity = userToModify.get();
            userEntity.setEmpireToken(empireToken.getToken());
            this.userRepository.save(userEntity);
            return this.userMapper.userEntityToUserDto(userEntity);
        }

        throw new UserNotFoundException("User cannot be found");
    }

    public UserDTO revokeEmpireToken(String authToken) {
        String currentUser = tokenService.extractUsername(authToken);
        Optional<UserEntity> userToRevokeToken = userRepository.findByUsername(currentUser);
        UserEntity userEntity = null;

        if (userToRevokeToken.isPresent()) {
            userEntity = userToRevokeToken.get();
            userEntity.setEmpireToken("");

            this.userRepository.save(userEntity);
            return this.userMapper.userEntityToUserDto(userEntity);
        }

        throw new UserNotFoundException("User cannot be found");
    }

    public EmpireTokenDTO getEmpireToken(String authToken) {
        String currentUser = tokenService.extractUsername(authToken);
        Optional<UserEntity> user = userRepository.findByUsername(currentUser);

        if (user.isPresent()) {
            StringBuilder displayToken = new StringBuilder();
            EmpireTokenDTO empireTokenDTO = new EmpireTokenDTO();
            String userToken = user.get().getEmpireToken();

            if (userToken == null || userToken.isEmpty()) {
                empireTokenDTO.setToken("No Empire API Key Found");
                return empireTokenDTO;
            }

            displayToken.append(userToken, 0, 4);
            displayToken.append("*".repeat(28));

            empireTokenDTO.setToken(displayToken.toString());
            return empireTokenDTO;
        }

        throw new UserNotFoundException("User cannot be found");
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
