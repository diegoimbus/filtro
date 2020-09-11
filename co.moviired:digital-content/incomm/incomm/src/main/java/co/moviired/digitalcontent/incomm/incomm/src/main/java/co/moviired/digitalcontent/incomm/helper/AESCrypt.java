package co.moviired.digitalcontent.incomm.helper;

import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * Utility to Encrypt / Decrypt information, using the AES algorithm
 */

@Slf4j
public final class AESCrypt implements Serializable {
    private static final long serialVersionUID = 442702418946297752L;

    private static final String KEY = "kYV7Pn5_0i RjOWtF=-peADr6ZXsvN"
            + "wSl9zq2gLoBaGh3HIu4fbKmU8dMETQcC1yxJ";

    private AESCrypt() {
        super();
    }

    // SERVICE METHODS

    public static String crypt(@NotNull String sinCifrar) {
        StringBuilder sb = new StringBuilder(sinCifrar.length());
        for (char c : sinCifrar.toCharArray())
            sb.append(KEY.charAt((int) c - 32));

        return sb.toString();
    }

    public static String decrypt(String claveCifrada) {
        StringBuilder sb = new StringBuilder(claveCifrada.length());
        for (char c : claveCifrada.toCharArray())
            sb.append((char) (KEY.indexOf(c) + 32));

        return sb.toString();
    }

}

