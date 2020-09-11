package co.moviired.transpiler.helper;

import co.moviired.transpiler.conf.GlobalProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public final class AESCrypt implements Serializable {
    private static final int SHIFT_COUNT = 13;
    private static final String CHARACTERS = "L:aE-N,A*Gb@B8cFCQd#DK5.MUeÉ/fg %HÍ2Yh0iÓjk6lm3J9no1ÚpZqXrsÁtVuI4vW_wOxySzáPé;íRóú=7T+";

    private static String allowedCharacters;
    private static String shiftChars;

    public static void init(GlobalProperties globalProperties) {
        StringBuilder allowedCharactersBuilder = new StringBuilder();
        for (int i = 0; i < CHARACTERS.length(); i++) {
            if (globalProperties.getPinAllowedCharacters().contains(String.valueOf(CHARACTERS.charAt(i)))) {
                allowedCharactersBuilder.append(CHARACTERS.charAt(i));
            }
        }
        allowedCharacters = allowedCharactersBuilder.toString();
        shiftChars = allowedCharacters.substring(SHIFT_COUNT).concat(allowedCharacters.substring(0, SHIFT_COUNT));
    }

    public static String crypt(String text) {
        return doCryptDecrypt(text, true);
    }

    public static String decrypt(String cypherText) {
        return doCryptDecrypt(cypherText, false);
    }

    private static String doCryptDecrypt(String text, boolean isEncrypt) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            sb.append(isEncrypt
                    ? shiftChars.charAt(allowedCharacters.indexOf(c))
                    : allowedCharacters.charAt(shiftChars.indexOf(c)));
        }
        return sb.toString();
    }

    private AESCrypt() {
        // Not is necessary this implementation
    }


}


