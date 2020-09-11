package com.moviired.helper;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import static com.moviired.helper.Constant.HEXADECIMAL_BYTE;

@Data
public class SHA1 {
    // Constantes
    private static final Logger LOGGER = LogManager.getLogger(SHA1.class.getName());
    private String texto = "";
    private Algoritmo algoritmo;

    // Genera el hash del texto definido en la clase
    public final String generateHash(String ptexto) {
        this.texto = ptexto;
        // Variable que almacena el criptograma generado
        String hash = "";
        // Variable que guardara el digest generado
        byte[] digest;
        // Variable que obtiene el buffer del texto
        byte[] buffer = texto.getBytes(StandardCharsets.UTF_8);

        switch (algoritmo) {
            case MD2:
            case MD5:
            case SHA1:
            case SHA256:
            case SHA512:
                // Se intenta obtener el Message Digest del algoritmo
                // seleccionado. Esto es en base a la clase MessageDigest
                // del paquete Security de Java
                try {
                    // Instancio un objeto MessageDigest con el algoritmo apropiado
                    MessageDigest md = MessageDigest.getInstance(algoritmo
                            .getNombre());
                    // Reseteo el digest que pueda existir en el objeto
                    md.reset();
                    // Envio el buffer el mensaje a encriptar
                    md.update(buffer);
                    // Obtengo el Digest del Message
                    digest = md.digest();
                    // Obtengo la cadena del hash en valores hexadecimales
                    hash = toHexadecimal(digest);
                } catch (NoSuchAlgorithmException e) {
                    // Controlo el mensaje de cualquier excepcion generada
                    LOGGER.error("Ocurrio un error al generar el hash: %s", e.getMessage());
                }
                break;

            case CRC32:
                // Obtengo el CRC32 del texto ingresado
                hash = getCRC32();
                break;
        }
        // retorna el criptograma generado
        return hash;
    }

    // Para los algoritmos que tienen hash, hace la conversion del arreglo de bytes[] generado a una cadena String
    private String toHexadecimal(byte[] digest) {
        StringBuilder hash = new StringBuilder();
        for (byte aux : digest) {
            int b = aux & HEXADECIMAL_BYTE; // Hace un cast del byte a hexadecimal
            if (Integer.toHexString(b).length() == 1) {
                hash.append("0");
            }
            hash.append(Integer.toHexString(b));
        }
        return hash.toString();
    }

    // Obtiene el Checksum 32 (CRC32) del texto ingresado
    private String getCRC32() {
        String hash;
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        // Objeto Checksum
        Checksum crc32 = new CRC32();
        // Inicializo el objeto
        crc32.reset();
        // Actualizo la con el arreglo de bytes que obtengo del texto
        crc32.update(bytes, 0, bytes.length);
        hash = Long.toHexString(crc32.getValue()).toUpperCase();
        return hash;
    }

    // Enumeracion de algoritmos soportados por la clase Criptography
    public enum Algoritmo {
        // Declaracion del nombre de cada algoritmo
        MD2("MD2"), MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"), SHA512(
                "SHA-512"), CRC32("CRC32");

        // nombre del algoritmo
        private String nombre;

        // Constructor que inicializa los nombres de los algoritmos disponibles
        Algoritmo(String pnombre) {
            this.nombre = pnombre;
        }

        // Devuelve el nombre del algoritmo que se esta usando
        private String getNombre() {
            return nombre;
        }
    }

}

