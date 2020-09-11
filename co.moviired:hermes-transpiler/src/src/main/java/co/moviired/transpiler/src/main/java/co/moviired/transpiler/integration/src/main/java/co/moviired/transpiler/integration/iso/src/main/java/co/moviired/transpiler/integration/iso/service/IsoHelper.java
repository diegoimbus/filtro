/*
 * IsoHelper.java
 */
package co.moviired.transpiler.integration.iso.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public final class IsoHelper implements Serializable {
    private static final long serialVersionUID = -5347090013441485308L;

    private static final int HEXA_RADIX = 16;
    private static final int BIN_RADIX = 2;
    private static final int MTI = 0;

    private IsoHelper() {
        super();
    }

    public static String getRequestData(InputStream entrada, StringBuilder longitudHexa) {
        StringBuilder dataRequest = new StringBuilder();
        int longitud = 0;
        try {
            char t = (char) entrada.read();
            if (StringUtils.isAsciiPrintable(String.valueOf(t))) {
                longitudHexa.append(t);
                longitudHexa.append((char) entrada.read());
                longitudHexa.append((char) entrada.read());
            }

            if (!StringUtils.isEmpty(longitudHexa) && StringUtils.isAlphanumeric(longitudHexa)) {
                longitud = Integer.parseInt(longitudHexa.toString(), HEXA_RADIX);
            }

            if (longitud > 0) {
                for (int i = 0; i < longitud; i++) {
                    dataRequest.append((char) entrada.read());
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return (longitud == 0) ? "" : dataRequest.substring(0, longitud);
    }

    private static String hexToBin(String hexaString) {
        return new BigInteger(hexaString, HEXA_RADIX).toString(BIN_RADIX);
    }

    private static List<Integer> getIsoFieldFromMessagePack(String isoMessagePack) {
        List<Integer> isoFields = new ArrayList<>();
        boolean bitmapSecondary = false;
        String msb;

        String isoBitMap = isoMessagePack.substring(4, 20);
        msb = StringUtils.leftPad(hexToBin(isoBitMap.substring(0, 1)), 4, '0');
        if (msb.startsWith("1")) {
            bitmapSecondary = true;
            isoBitMap = isoMessagePack.substring(4, 36);
        }

        String[] validaHexaField = new String[isoBitMap.length() / 2];
        char[] hexaPos = isoBitMap.toCharArray();

        validaHexaField[0] = String.valueOf(hexaPos[0]) + hexaPos[1];
        validaHexaField[1] = String.valueOf(hexaPos[2]) + hexaPos[3];
        validaHexaField[2] = String.valueOf(hexaPos[4]) + hexaPos[5];
        validaHexaField[3] = String.valueOf(hexaPos[6]) + hexaPos[7];
        validaHexaField[4] = String.valueOf(hexaPos[8]) + hexaPos[9];
        validaHexaField[5] = String.valueOf(hexaPos[10]) + hexaPos[11];
        validaHexaField[6] = String.valueOf(hexaPos[12]) + hexaPos[13];
        validaHexaField[7] = String.valueOf(hexaPos[14]) + hexaPos[15];

        if (bitmapSecondary) {
            validaHexaField[8] = String.valueOf(hexaPos[16]) + hexaPos[17];
            validaHexaField[9] = String.valueOf(hexaPos[18]) + hexaPos[19];
            validaHexaField[10] = String.valueOf(hexaPos[20]) + hexaPos[21];
            validaHexaField[11] = String.valueOf(hexaPos[22]) + hexaPos[23];
            validaHexaField[12] = String.valueOf(hexaPos[24]) + hexaPos[25];
            validaHexaField[13] = String.valueOf(hexaPos[26]) + hexaPos[27];
            validaHexaField[14] = String.valueOf(hexaPos[28]) + hexaPos[29];
            validaHexaField[15] = String.valueOf(hexaPos[30]) + hexaPos[31];
        }

        int binPosCont = 1;
        for (int i = 0; i < validaHexaField.length; i++) {
            validaHexaField[i] = StringUtils.leftPad(Integer.toBinaryString(Integer.parseInt(validaHexaField[i], 16)), 8, '0');
            for (int j = 0; j < 8; j++) {
                if (validaHexaField[i].toCharArray()[j] == '1') {
                    isoFields.add(binPosCont);
                }
                binPosCont++;
            }
        }

        return isoFields;
    }

    public static Map<Integer, String> getDataIsoFieldFromMessage(ISOBasePackager isoPackager, String isoMessage) throws ISOException {
        HashMap<Integer, String> hmap = new HashMap<>();

        List<Integer> isoFields = getIsoFieldFromMessagePack(isoMessage);
        byte[] isoMessagePack = isoMessage.getBytes(Charset.defaultCharset());

        ISOMsg isoMessageUnpack = new ISOMsg();
        isoMessageUnpack.setPackager(isoPackager);
        isoMessageUnpack.unpack(isoMessagePack);
        isoMessageUnpack.recalcBitMap();

        // Cargar la data
        hmap.put(MTI, isoMessageUnpack.getMTI());
        for (int field : isoFields) {
            hmap.put(field, isoMessageUnpack.getString(field));
        }

        return hmap;
    }

    public static void logISOMsg(Map<Integer, String> msg) {
        log.info("----ISO MESSAGE-----");
        TreeSet<Integer> keys = new TreeSet<>(msg.keySet());
        for (Integer key : keys) {
            String value = msg.get(key);
            if (value != null) {
                log.info("  FIELD {}: '{}'", StringUtils.leftPad(key.toString(), 2, ' '), value);
            }
        }
        log.info("--------------------");
    }

}
