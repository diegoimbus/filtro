package co.moviired.microservice.repository;

import co.moviired.base.domain.exception.CommunicationException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.constants.ConstantNumbers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.BASE24Channel;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;

@Slf4j
@Component
@AllArgsConstructor
public class BankingBogotaRepository implements Serializable {

    private final BankProperties bankProperties;
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

    public BASE24Channel openSockeConnection(GenericPackager packager, String ip, Integer port, Integer connectionTimeout) throws CommunicationException {
        try {
            BASE24Channel nacChannel = new BASE24Channel(ip, port, packager);
            nacChannel.setTimeout(connectionTimeout);
            nacChannel.connect();
            return nacChannel;
        } catch (IOException e) {
            throw new CommunicationException("-1", "Error de conexión, no se pudo conectar o se supero el limite de espera", e);
        }
    }

    public void sendRequest(BASE24Channel nacChannel, ISOMsg isoMsg) throws CommunicationException, ProcessingException {
        try {
            String trama = new String(isoMsg.pack());

            int lenght = trama.length();
            String hex = Integer.toHexString(lenght);
            hex = hex.toUpperCase();
            trama = StringUtils.leftPad(hex, ConstantNumbers.LENGTH_3, "0") + trama;

            // Enviar la petición
            log.info("Request SWITCH --> " + trama);
            logISOMsg(isoMsg);
            if (nacChannel.isConnected()) {
                nacChannel.send(isoMsg);
            } else {
                log.error("El Socket se ha desconectado");
            }

        } catch (ISOException e) {
            throw new ProcessingException("-1", "Error al construir la trama ISO");
        } catch (IOException e) {
            throw new CommunicationException("-1", "Error al realizar el envio del mensaje");
        }
    }

    public ISOMsg getResponse(BASE24Channel nacChannel) throws CommunicationException, ProcessingException {
        ISOMsg isoMsg = null;
        try {
            if (nacChannel.isConnected()) {
                Integer readTimeout = bankProperties.getBogotaTimeoutRead();
                long initSeconds = System.currentTimeMillis();
                nacChannel.setTimeout(readTimeout);
                isoMsg = nacChannel.receive();
                long timeRead = System.currentTimeMillis() - initSeconds;

                if (timeRead > readTimeout) {
                    throw new SocketException("Tiempo de conexión superado para recibir información la transacción...[BANCO BOGOTA]");
                }
                log.info("Lectura exitosa del Banco de Bogota");
                log.info("Tiempo empleado en leer la respuesta Banco de Bogota = " + timeRead + " [Tiempo maximo configurado = " + readTimeout + " ].");

                logISOMsg(isoMsg);
                nacChannel.disconnect();
            } else {
                log.error("El Socket se ha desconectado");
            }
            return isoMsg;

        } catch (ISOException e) {
            throw new ProcessingException("-1", "Error al recibir el mensaje empaquetado");
        } catch (IOException e) {
            throw new CommunicationException("-1", "Error de conexión, no se pudo leer la respuesta o supero el limite de espera");
        }
    }

}

