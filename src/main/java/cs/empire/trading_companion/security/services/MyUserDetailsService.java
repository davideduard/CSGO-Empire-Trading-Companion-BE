package cs.empire.trading_companion.security.services;

import cs.empire.trading_companion.entities.UserEntity;
import cs.empire.trading_companion.repositories.AuthRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;

    public MyUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = authRepository.findByUsername(username);
        Optional<UserEntity> userByEmail = authRepository.findByEmail(username);

        if (user.isPresent()) {
            return User.builder()
                    .username(user.get().getUsername())
                    .password(user.get().getPassword())
                    .build();
        }

        if (userByEmail.isPresent()) {
            return User.builder()
                    .username(userByEmail.get().getUsername())
                    .password(userByEmail.get().getPassword())
                    .build();
        }

        throw new UsernameNotFoundException("username is not available");
    }
}
