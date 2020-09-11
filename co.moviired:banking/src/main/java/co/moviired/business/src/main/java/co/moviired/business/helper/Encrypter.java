package co.moviired.business.helper;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class Encrypter {

    private Encrypter() {
        super();
    }

    public static String encryptAES(String plainData, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String appKey = processKey(key);

        int len = plainData.length();
        int q = len / 16;
        int addSpaces = (q + 1) * 16 - len;
        plainData = plainData.concat(" ".repeat(addSpaces));
        SecretKeySpec keySpec = new SecretKeySpec(appKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(1, keySpec);
        byte[] encrypted = cipher.doFinal(plainData.getBytes());

        return bytesToHex(encrypted);
    }

    public static String decryptAES(String encrData, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String appKey = processKey(key);
        SecretKeySpec keySpec = new SecretKeySpec(appKey.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(2, keySpec);

        byte[] outText = cipher.doFinal(hexToBytes(encrData));

        return new String(outText).trim();

    }

    private static String processKey(String key) {
        StringBuilder appKey = new StringBuilder(key);

        int len = key.length();
        if (len < 16) {
            int addSpaces = 16 - len;
            appKey.append(" ".repeat(addSpaces));
        } else {
            appKey = new StringBuilder(key.substring(0, 16));
        }
        appKey.trimToSize();

        return appKey.toString();
    }

    private static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder str = new StringBuilder();
        for (byte b : data) {
            if ((b & 0xFF) < 16) {
                str.append("0");
            }
            str.append(String.format("%02X", b));
        }
        str.trimToSize();
        return str.toString();
    }

    private static byte[] hexToBytes(String str) {
        if ((str == null) || (str.length() < 2)) {
            return new byte[0];
        }

        int len = str.length() / 2;
        byte[] buffer = new byte[len];
        for (int i = 0; i < len; i++) {
            buffer[i] = ((byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16));
        }
        return buffer;
    }
}




