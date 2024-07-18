package cs.empire.trading_companion.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
}
