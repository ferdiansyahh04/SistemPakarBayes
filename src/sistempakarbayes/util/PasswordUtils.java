package sistempakarbayes.util;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hashing password dengan PBKDF2-SHA256 (aman, tanpa plaintext di DB).
 * Format tersimpan:  iterasi:salt_hex:hash_hex
 * Contoh:           10000:a1b2c3d4...:bfacd14c...
 */
public final class PasswordUtils {

    private static final int ITERASI = 10000;
    private static final int KEYLEN_BIT = 256;

    private PasswordUtils() {
    }

    /**
     * Hash password menjadi string "iterasi:salt:hash".
     * Salt dibuat acak setiap pemanggilan.
     */
    public static String hash(String password) {
        try {
            byte[] salt = generateSalt();
            byte[] dk = pbkdf2(password.toCharArray(), salt, ITERASI, KEYLEN_BIT);
            return ITERASI + ":" + toHex(salt) + ":" + toHex(dk);
        } catch (Exception e) {
            throw new IllegalStateException("Gagal melakukan hash password.", e);
        }
    }

    /**
     * Verifikasi password terhadap string hash yang tersimpan.
     * Menggunakan comparison waktu konstan (MessageDigest.isEqual).
     */
    public static boolean verifikasi(String password, String tersimpan) {
        if (tersimpan == null) return false;
        String[] bagian = tersimpan.split(":");
        if (bagian.length != 3) return false;
        try {
            int iter = Integer.parseInt(bagian[0]);
            byte[] salt = fromHex(bagian[1]);
            byte[] expected = fromHex(bagian[2]);
            byte[] actual = pbkdf2(password.toCharArray(), salt, iter, KEYLEN_BIT);
            return java.security.MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] pw, byte[] salt, int iter, int keyLenBit)
            throws Exception {
        PBEKeySpec spec = new PBEKeySpec(pw, salt, iter, keyLenBit);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return f.generateSecret(spec).getEncoded();
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return out;
    }
}