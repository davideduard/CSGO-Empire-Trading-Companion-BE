package cs.empire.trading_companion.controllers;

import cs.empire.trading_companion.dtos.LoginRequestDTO;
import cs.empire.trading_companion.dtos.LoginResponseDTO;
import cs.empire.trading_companion.dtos.RegisterResponseDTO;
import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.services.AuthService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO userToken = this.authService.loginUser(loginRequestDTO);
        return ResponseEntity.ok(userToken);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@RequestBody UserDTO userDTO) {
        RegisterResponseDTO userToken = authService.registerUser(userDTO);
        return ResponseEntity.ok(userToken);
    }
}
