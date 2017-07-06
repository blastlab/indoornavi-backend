package co.blastlab.serviceblbnavi.utils;

import co.blastlab.serviceblbnavi.domain.User;
import org.jboss.resteasy.util.Base64;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AuthUtils {
	public static byte[] getSalt() throws NoSuchAlgorithmException
	{
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	public static String get_SHA_256_Password(String passwordToHash, byte[] salt)
	{
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] bytes = md.digest(passwordToHash.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return generatedPassword;
	}

	public static void comparePasswords(String password, User user) throws AuthenticationException, InvalidPasswordException {
		String saltedPassword;
		try {
			saltedPassword = AuthUtils.get_SHA_256_Password(password, Base64.decode(user.getSalt()));
			if (!saltedPassword.equalsIgnoreCase(user.getPassword())) {
				throw new InvalidPasswordException();
			}
		} catch (IOException e) {
			throw new AuthenticationException();
		}
	}

	public static class AuthenticationException extends Throwable {}

	public static class InvalidPasswordException extends Throwable {}
}
