package co.blastlab.serviceblbnavi.security;

import org.apache.commons.lang.RandomStringUtils;

/**
 * PasswordEncoder generates authorization token.
 * <p>
 * @author Michał Koszałka
 */
public class PasswordEncoder {

    private static final int AUTH_TOKEN_LENGTH = 32;

    public static String getAuthToken() {
        return RandomStringUtils.randomAlphanumeric(AUTH_TOKEN_LENGTH);
    }

}
