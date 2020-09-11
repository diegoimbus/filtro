package co.moviired.microservice.repository;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import co.moviired.microservice.domain.constants.ConstantSwitch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.stereotype.Repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.Serializable;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

@Repository
@Slf4j
public class BankingSwitchRepository implements Serializable {


    private static final String FORMATTED_LOG_5 = "{} {} {} {} {}";
    private static final String FORMATTED_LOG_4 = "{} {} {} {}";

    private static void logISOMsg(String correlationID, ISOMsg msg) {
        log.info(FORMATTED_LOG_4, "[", correlationID, "]", "----ISO MESSAGE-----");
        try {
            log.info("{} {} ", "  MTI : ", msg.getMTI());
            for (int i = 1; i <= msg.getMaxField(); i++) {
                if (msg.hasField(i)) {
                    log.info(FORMATTED_LOG_4, "    Field-", i, ": ", msg.getString(i));
                }
            }
        } catch (ISOException e) {
            log.error(e.getMessage());
        } finally {
            log.info("--------------------");
        }

    }

    public ASCIIChannel openSockeConnection(GenericPackager packager, String ip, Integer port, Integer connectionTimeout) throws IOException {
        ASCIIChannel nacChannel = new ASCIIChannel(ip, port, packager);
        nacChannel.setTimeout(connectionTimeout);
        nacChannel.connect();

        return nacChannel;
    }

    public void closeSocketConnection(ASCIIChannel connection) {
        try {
            if (connection != null) {
                connection.disconnect();
            }

        } catch (Exception exc) {
            log.error("{} {}", "Error cerrando socket de comunicación. Causa: ", exc);
        }
    }

    public void sendRequest(ASCIIChannel nacChannel, ISOMsg isoMsg, String correlationId) throws ISOException, IOException {
        // Transformar la trama a enviar
        String trama = new String(isoMsg.pack());

        int lenght = trama.length();
        String hex = Integer.toHexString(lenght);
        hex = hex.toUpperCase();
        trama = StringUtils.leftPad(hex, ConstantSwitch.LENGTH_3, "0") + trama;

        // Enviar la petición
        log.info(FORMATTED_LOG_5, "[", correlationId, "]", "Request SWITCH --> ", trama);
        logISOMsg(correlationId, isoMsg);
        if (nacChannel.isConnected()) {
            BufferedOutputStream outStream = new BufferedOutputStream(nacChannel.getSocket().getOutputStream());
            outStream.write(trama.getBytes());
            outStream.flush();
        } else {
            log.error(FORMATTED_LOG_4, "[", correlationId, "]", "El Socket se ha desconectado");
        }
    }

    public ISOMsg getResponse(ASCIIChannel nacChannel, String correlationId) throws ISOException, IOException {
        ISOMsg isoMsg = null;

        if (nacChannel.isConnected()) {

            // Leer la respuesta
            BufferedInputStream reader = new BufferedInputStream(nacChannel.getSocket().getInputStream());
            byte[] data = new byte[ConstantSwitch.LENGTH_RESPONSE];
            if (reader.read(data) != -1) {
                String resp = new String(data);
                resp = resp.substring(ConstantSwitch.LENGTH_3);
                data = resp.getBytes();

                // Validar la respuesta
                log.info(FORMATTED_LOG_5, "[", correlationId, "]", "Response SWITCH --> ", resp);

                log.info(FORMATTED_LOG_5, "[", correlationId, "]", "length response: ", resp.length());
                if (resp.length() < ConstantSwitch.LENGTH_VALIDATE_RESPONSE) {
                    throw new ISOException("[CORRELATION ID]" + "La longitud de la respuesta recibida es incorrecta. Esperada: 168 bytes. Recibida: " + resp.length() + " bytes");
                }

                // Transformar a Iso Message
                isoMsg = new ISOMsg("0210");
                isoMsg.setPackager(nacChannel.getPackager());
                isoMsg.unpack(data);
                logISOMsg(correlationId, isoMsg);
            }
        } else {
            log.error(FORMATTED_LOG_4, "[", correlationId, "]", "El Socket se ha desconectado");
        }

        return isoMsg;
    }

}


