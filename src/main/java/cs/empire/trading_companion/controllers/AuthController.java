package cs.empire.trading_companion.controllers;

import cs.empire.trading_companion.dtos.LoginRequestDTO;
import cs.empire.trading_companion.dtos.LoginResponseDTO;
import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.services.AuthService;
import cs.empire.trading_companion.security.services.JWTService;
import cs.empire.trading_companion.security.services.MyUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final MyUserDetailsService userDetailsService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JWTService jwtService, MyUserDetailsService userDetailsService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(), loginRequestDTO.getPassword()
                ));

        if (authentication.isAuthenticated()) {
            LoginResponseDTO response = new LoginResponseDTO();
            String token = jwtService.generateToken(userDetailsService.loadUserByUsername(loginRequestDTO.getUsername()));
            response.setToken(token);
            return ResponseEntity.ok(response);
        } else {
            throw new UsernameNotFoundException("Invalid Credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = authService.saveUser(userDTO);
        return ResponseEntity.ok(savedUser);
    }
}
