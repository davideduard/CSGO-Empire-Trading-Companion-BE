package cs.empire.trading_companion.controllers;

import cs.empire.trading_companion.dtos.EmpireTokenDTO;
import cs.empire.trading_companion.dtos.UserDTO;
import cs.empire.trading_companion.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody UserDTO newUser) {
        String token = extractToken(authHeader);
        return ResponseEntity.ok(this.userService.updateUser(newUser, token));
    }

    @PostMapping("/set-empire-token")
    public ResponseEntity<UserDTO> setEmpireToken(@RequestHeader("Authorization") String authHeader, @RequestBody EmpireTokenDTO empireToken) {
        String authToken = extractToken(authHeader);
        return ResponseEntity.ok(this.userService.setEmpireToken(empireToken, authToken));
    }

    @PostMapping("/revoke-empire-token")
    public ResponseEntity<UserDTO> revokeEmpireToken(@RequestHeader("Authorization") String authHeader) {
        String authToken = extractToken(authHeader);
        return ResponseEntity.ok(this.userService.revokeEmpireToken(authToken));
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

}
