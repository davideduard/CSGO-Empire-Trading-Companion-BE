package cs.empire.trading_companion.repositories;

import cs.empire.trading_companion.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
