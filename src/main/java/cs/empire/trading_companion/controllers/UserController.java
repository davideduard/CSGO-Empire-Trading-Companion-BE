package cs.empire.trading_companion.controllers;

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

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO foundUser = this.userService.getUserByEmail(email);
        return ResponseEntity.ok(foundUser);
    }
}
