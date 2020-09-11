package co.moviired.microservice.repository;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import co.moviired.microservice.constants.ConstantSwitch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.BASE24Channel;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

@Component
@Slf4j
public class BankingBogotaRepository implements Serializable {

    private static final String FORMATTED_LOG_4 = "{} {} {} {}";

    private static void logISOMsg(ISOMsg msg) {
        log.info("----ISO MESSAGE-----");
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

    public BASE24Channel openSockeConnection(GenericPackager packager, String ip, Integer port, Integer connectionTimeout) throws IOException {
        BASE24Channel nacChannel = new BASE24Channel(ip, port, packager);
        nacChannel.setTimeout(connectionTimeout);
        nacChannel.connect();
        return nacChannel;
    }

    public void sendRequest(BASE24Channel nacChannel, ISOMsg isoMsg) throws ISOException, IOException {
        // Transformar la trama a enviar
        String trama = new String(isoMsg.pack());

        int lenght = trama.length();
        String hex = Integer.toHexString(lenght);
        hex = hex.toUpperCase();
        trama = StringUtils.leftPad(hex, ConstantSwitch.LENGTH_3, "0") + trama;

        // Enviar la petición
        log.info("Request SWITCH --> " + trama);
        logISOMsg(isoMsg);
        if (nacChannel.isConnected()) {
            nacChannel.send(isoMsg);
        } else {
            log.error("El Socket se ha desconectado");
        }
    }

    public ISOMsg getResponse(BASE24Channel nacChannel) throws ISOException, IOException {
        ISOMsg isoMsg = null;
        if (nacChannel.isConnected()) {
            isoMsg = nacChannel.receive();
            logISOMsg(isoMsg);
        } else {
            log.error("El Socket se ha desconectado");
        }
        return isoMsg;
    }

}



