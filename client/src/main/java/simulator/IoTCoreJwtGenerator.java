package simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class IoTCoreJwtGenerator {

    public static enum Algorithm {
        RS256, EC256;
    }

    private static class ValidPeriod {
        Date start;
        Date expire;
    }

    private Algorithm algorithm;
    private String projectId;
    private PrivateKey privateKey;

    public String generateJwt(int validInMinutes) {
        ValidPeriod validPeriod = generatePeriod(validInMinutes);

        JwtBuilder jwtBuilder = Jwts.builder()
        .setIssuedAt(validPeriod.start)
        .setExpiration(validPeriod.expire)
        .setAudience(this.projectId);

        return jwtBuilder.signWith(this.privateKey, getSignatureAlgorithm()).compact();
    }

    private PrivateKey createPrivateKey(String prtKeyPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(prtKeyPath));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = getKeyFactory();
        return kf.generatePrivate(spec);
    }

    private KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
        KeyFactory toReturn = null;
        switch (this.algorithm) {
        case RS256:
            toReturn = KeyFactory.getInstance("RSA");
            break;
        case EC256:
            toReturn = KeyFactory.getInstance("EC");
            break;
        }
        return toReturn;
    }

    private SignatureAlgorithm getSignatureAlgorithm() {
        SignatureAlgorithm toReturn = null;
        switch (this.algorithm) {
        case RS256:
            toReturn = SignatureAlgorithm.RS256;
            break;
        case EC256:
            toReturn = SignatureAlgorithm.ES256;
            break;
        }
        return toReturn;
    }

    private ValidPeriod generatePeriod(int validInMinutes) {
        ValidPeriod validPeriod = new ValidPeriod();

        validPeriod.start = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(validPeriod.start);
        calendar.add(Calendar.MINUTE, validInMinutes);

        validPeriod.expire = calendar.getTime(); 
        
        return validPeriod;
    }

    private IoTCoreJwtGenerator() {}

    public static class Builder {
        private Algorithm algorithm;
        private String projectId;
        private String prtKeyPath;

        public Builder(String projectId) {
            this.projectId = projectId;
        }

        public Builder withAlgorithm(String algorithm) {
            this.algorithm = Algorithm.valueOf(algorithm);
            return this;
        }

        public Builder withPrivateKey(String prtKeyPath) {
            this.prtKeyPath = prtKeyPath;
            return this;
        }

        public IoTCoreJwtGenerator build() {
            IoTCoreJwtGenerator generator = new IoTCoreJwtGenerator();
            generator.algorithm = this.algorithm;
            generator.projectId = this.projectId;
            try {
                generator.privateKey = generator.createPrivateKey(this.prtKeyPath);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid private key path provided");
            }
            return generator;
        }
    }
}