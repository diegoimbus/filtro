package co.moviired.microservice.helper;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Slf4j
public final class UtilHelper {

    private UtilHelper() {
        super();
    }

    public static boolean isNumeric(String str) {
        return ((str != null) && (!str.trim().isEmpty()) && (str.matches("[+-]?\\d*(\\.\\d+)?")));
    }

    public static boolean estaEnArreglo(String[] arreglo, String valor) {
        boolean resultado = false;
        for (String value : arreglo) {
            if (value.equals(valor)) {
                resultado = true;
                break;
            }
        }


        return resultado;
    }

    public static String strPad(String source, Integer length, String character, Integer direction) {
        String complete;
        if (direction == 0) {
            complete = StringUtils.leftPad(source, length, character);
        } else {
            complete = StringUtils.rightPad(source, length, character);
        }

        return complete;
    }


    public static Properties getPropertiesFile(String rutaArchivo) {
        Properties lproperties = new Properties();
        try (InputStream in = new FileInputStream(rutaArchivo)) {
            lproperties.load(in);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return lproperties;
    }

    public static byte[] getBytesFromFile(String fileName) throws IOException {
        byte[] bytesArch = null;
        if (fileName != null) {
            File file = new File(fileName);
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                // Obtener el flujo de bytes del archivo
                bytesArch = new byte[(int) file.length()];
                int lecturaValida = fileInputStream.read(bytesArch);
                if (lecturaValida < 0) {
                    throw new IOException("Lectura invalida");
                }
            } catch (Exception e) {
                throw new IOException("ERROR: " + e.getMessage());
            }
        }

        return bytesArch;
    }

    public static String getDateInformation(Date currentDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(currentDate);
    }

}

