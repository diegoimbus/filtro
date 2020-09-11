package co.moviired.supportp2pvalidatortransaction.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

import static co.moviired.supportp2pvalidatortransaction.util.Constants.AES;
import static co.moviired.supportp2pvalidatortransaction.util.Constants.MD5;

@Slf4j
public class CypherHelper {

    public static String encrypt(String text, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance(MD5)
                    .digest(secretKey.getBytes(StandardCharsets.UTF_8)), 24), AES));
            return new String(Base64.encodeBase64(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            log.error("Error occurred encrypting text: {}", e.getMessage());
            return "";
        }
    }

    public static String decrypt(String encryptText, String secretKey) {
        try {
            Cipher decipher = Cipher.getInstance(AES);
            decipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance(MD5)
                    .digest(secretKey.getBytes(StandardCharsets.UTF_8)), 24), AES));
            return new String(decipher.doFinal(Base64.decodeBase64(encryptText.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Error occurred decrypting text: {}", e.getMessage());
            return "";
        }

    }

    private CypherHelper() {
        // Not is necessary this implementation
    }
}

