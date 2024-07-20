package cs.empire.trading_companion;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class JWTSecretMakerTest {

    @Test
    public void generateSecretKey() {
        SecretKey secretKey = Jwts.SIG.HS512.key().build();
        String encodedKey = DatatypeConverter.printHexBinary(secretKey.getEncoded());
        System.out.printf("\nkey = [%s] \n", encodedKey);
    }
}
