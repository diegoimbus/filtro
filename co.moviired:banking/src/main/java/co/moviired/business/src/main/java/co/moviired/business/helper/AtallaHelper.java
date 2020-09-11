package co.moviired.business.helper;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.business.properties.AtallaProperties;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class AtallaHelper {

    private AtallaHelper() {
        super();
    }

    public static String encryptAtalla(@NotNull String walletNumber,
                                       @NotNull String token,
                                       @NotNull AtallaProperties atallaProperties)
            throws DataException {

        String encryptOTP = "";
        StringBuilder identification = new StringBuilder();
        if (walletNumber.length() > 10) {
            identification.append(walletNumber.substring(walletNumber.length() - 10));
        } else {
            identification.append(walletNumber);
        }

        while (identification.length() < 10) {
            identification.insert(0, "0");
        }

        StringBuilder bindAgrarioTemp = new StringBuilder(atallaProperties.getBindAgrario());
        bindAgrarioTemp.append(identification);
        String pan = bindAgrarioTemp.substring(bindAgrarioTemp.length() - 13, bindAgrarioTemp.length() - 1);

        if (atallaProperties.isUseAtalla()) {
            String answerAtalla = atallaEncryptPin(token, pan, atallaProperties);
            if (answerAtalla == null) {
                throw new DataException("400", "Atalla no response");
            }

            encryptOTP = answerAtalla;
        }

        return encryptOTP;
    }


    private static String sendMessageAtalla(@NotNull String command,
                                            @NotNull AtallaProperties atallaProperties) throws ProcessingException {
        log.info("Conectandose al atalla... IP = " + atallaProperties.getAtallaIp() + " PORT = " + atallaProperties.getAtallaPuerto());

        try (Socket socket = new Socket(atallaProperties.getAtallaIp(), Integer.parseInt(atallaProperties.getAtallaPuerto()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            socket.setSoTimeout(Integer.parseInt(atallaProperties.getAtallaTimeout()));

            log.info("Mensaje enviado a atalla -> " + command);
            out.println(command);
            out.flush();
            String resp = in.readLine();
            if (resp.isEmpty()) {
                throw new ProcessingException("300", "Error al recibir respuesta de atalla: Respuesta vacia");
            }
            log.info("Respuesta Recibida desde Atalla:" + resp);

            return resp;

        } catch (Exception ex) {
            log.info(ex.toString());
            throw new ProcessingException("301", "Error en envio de mensaje a atalla para procesos de MAC");
        }
    }

    private static String atallaEncryptPin(@NotNull String token,
                                           @NotNull String walletNumber,
                                           @NotNull AtallaProperties atallaProperties)
            throws DataException {
        String response;

        try {
            // Armar la petición al Atalla
            String atallaKey = atallaProperties
                    .getKeinHeader()
                    .concat(",")
                    .concat(atallaProperties.getKeinCryptogram())
                    .concat(",")
                    .concat(atallaProperties.getKeinMac());

            StringBuilder command = new StringBuilder();
            command.append("<");
            command.append(atallaProperties.getComandoAtalla());
            command.append("#");
            command.append(atallaKey);
            command.append("#");
            command.append(token);
            command.append("#");
            command.append(walletNumber);
            command.append("#");
            command.append(">");
            command.trimToSize();

            // Enviar petición a Atalla
            response = sendMessageAtalla(command.toString(), atallaProperties);

            // Procesar la respuesta
            String[] atallaResponse = response.split("#");
            atallaResponse[0] = atallaResponse[0].replace("<", "");

            log.info("Respuesta Atalla : " + atallaResponse[0]);
            if (!atallaProperties.getAtallaCodigoExitoso().equalsIgnoreCase(atallaResponse[0])) {
                throw new DataException("400", "Atalla no responde código exitoso");
            }

            response = atallaResponse[1];

        } catch (Exception | ProcessingException ex) {
            throw new DataException("400", ex.getMessage());
        }

        return response;
    }


}

