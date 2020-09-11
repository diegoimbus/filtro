package co.moviired.digitalcontent.business.helper;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class EncriptPin implements Serializable {
    private static final long serialVersionUID = -7434246942992596190L;

    private static final String DE_SEDE_CBC_PKCS5_PADDING = "DESede/CBC/PKCS5Padding";
    private static final String DE_SEDE = "DESede";
    private static final String MD5 = "md5";

    private static final int VECTOR_LENGTH = 8;
    private static final int DEST_POS = 16;

    private EncriptPin() {
        super();
    }

    public static String encrypt(final String text, final String publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        final MessageDigest md = MessageDigest.getInstance(MD5);
        final byte[] digestOfPassword = md.digest(publicKey.getBytes(StandardCharsets.UTF_8));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        System.arraycopy(keyBytes, 0, keyBytes, DEST_POS, VECTOR_LENGTH);

        final SecretKey key = new SecretKeySpec(keyBytes, DE_SEDE);
        final IvParameterSpec iv = new IvParameterSpec(new byte[VECTOR_LENGTH]);
        final Cipher cipher = Cipher.getInstance(DE_SEDE_CBC_PKCS5_PADDING);

        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = text.getBytes(StandardCharsets.UTF_8);

        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        byte[] base64Bytes = Base64.encodeBase64(cipherText);

        return new String(base64Bytes);
    }

    public static String decrypt(String messages, String keys) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final MessageDigest md = MessageDigest.getInstance(MD5);
        final byte[] digestOfPassword = md.digest(keys.getBytes(StandardCharsets.UTF_8));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

        int j = 0;
        int k = DEST_POS;

        while (j < VECTOR_LENGTH) {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, DE_SEDE);
        final IvParameterSpec iv = new IvParameterSpec(new byte[VECTOR_LENGTH]);
        final Cipher decipher = Cipher.getInstance(DE_SEDE_CBC_PKCS5_PADDING);
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        final byte[] plainText = decipher.doFinal(Base64.decodeBase64(messages));

        return new String(plainText, StandardCharsets.UTF_8);
    }
}


