package politinder;

import java.security.*;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Passwords {
  private static final int ITERATIONS = 600_000;

  public static String hash(char[] password) throws Exception {
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    return "pbkdf2$"
        + ITERATIONS
        + "$"
        + Base64.getEncoder().encodeToString(salt)
        + "$"
        + Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS));
  }

  private static byte[] derive(char[] password, byte[] salt, int rounds) throws Exception {
    PBEKeySpec spec = new PBEKeySpec(password, salt, rounds, 256);
    try {
      return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    } finally {
      spec.clearPassword();
    }
  }

  public static boolean verify(char[] password, String encoded) throws Exception {
    try {
      String[] p = encoded.split("\\$");
      if (p.length != 4 || !p[0].equals("pbkdf2")) return false;
      int rounds = Integer.parseInt(p[1]);
      if (rounds < 1 || rounds > 2_000_000) return false;
      return MessageDigest.isEqual(
          Base64.getDecoder().decode(p[3]),
          derive(password, Base64.getDecoder().decode(p[2]), rounds));
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
