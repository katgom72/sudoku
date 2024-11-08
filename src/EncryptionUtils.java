import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256; // AES-256
    private static final int IV_SIZE = 16;  // IV for AES should be 16 bytes

    // Generowanie klucza AES
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // Generowanie losowego IV
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // Szyfrowanie tekstu
    public static String encrypt(String plaintext, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encrypted); // Zwracamy jako Base64
    }

    // Deszyfrowanie tekstu
    public static String decrypt(String ciphertext, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted);
    }
}
