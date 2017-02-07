package co.blastlab.serviceblbnavi.security;

import org.apache.commons.lang.RandomStringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordEncoder generates authorization token.
 * <p>
 * @author Michał Koszałka
 */
public class PasswordEncoder {

    private static final int AUTH_TOKEN_LENGTH = 32;
    private static final int SALT_LENGTH = 8;

    public static String getAuthToken() {
        return RandomStringUtils.randomAlphanumeric(AUTH_TOKEN_LENGTH);
    }

    public static String getSalt() {
        return RandomStringUtils.randomAlphanumeric(SALT_LENGTH);
    }

    public static String getShaPassword(String algorithm, String plainPassword, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(salt.getBytes());
            md.update(plainPassword.getBytes());

            byte byteData[] = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
