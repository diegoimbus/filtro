package co.moviired.microservice.repository;

import co.moviired.microservice.conf.BASE24TCPCERPChannel;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.constants.ConstantNumbers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketException;

@Slf4j
@Component
@AllArgsConstructor
public class BankingAgrarioRepository {

    private static final String FORMATTED_LOG_4 = "{} {} {} {}";
    private final BankProperties bankProperties;

    // Imprimir ISOMsg
    public void logISOMsg(ISOMsg msg) {
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

    // Abrir conexion al banco agrario
    public BASE24TCPCERPChannel openSockeConnection(GenericPackager packager, String ip, Integer port, Integer connectionTimeout) throws IOException {
        BASE24TCPCERPChannel nacChannel = new BASE24TCPCERPChannel(ip, port, packager);
        nacChannel.setName("operator-adaptorAgrario");
        nacChannel.setTimeout(connectionTimeout);
        nacChannel.connect();
        return nacChannel;
    }

    // Enviar la trama ISO al banco agrario
    public void sendRequest(BASE24TCPCERPChannel nacChannel, ISOMsg isoMsg) throws ISOException, IOException {
        String trama = new String(isoMsg.pack());

        int lenght = trama.length();
        String hex = Integer.toHexString(lenght);
        hex = hex.toUpperCase();
        trama = StringUtils.leftPad(hex, ConstantNumbers.LENGTH_3, "0") + trama;

        log.info("Request SWITCH --> " + trama);
        logISOMsg(isoMsg);
        if (nacChannel.isConnected()) {
            nacChannel.send(isoMsg);
        } else {
            log.error("El Socket se ha desconectado");
        }
    }

    //Obtener la respuesta del banco agrario
    public ISOMsg getResponse(BASE24TCPCERPChannel nacChannel) throws ISOException, IOException {
        ISOMsg isoMsg = null;
        if (nacChannel.isConnected()) {
            Integer readTimeout = bankProperties.getAgrarioReadTimeout();
            long initSeconds = System.currentTimeMillis();
            nacChannel.setTimeout(readTimeout);

            isoMsg = nacChannel.receive();
            long timeRead = System.currentTimeMillis() - initSeconds;
            if (timeRead > readTimeout) {
                throw new SocketException("Tiempo de conexión superado para recibir información la transacción...[BANCO AGRARIO]");
            }

            log.info("Lectura exitosa del Banco Agrario");
            log.info("Tiempo empleado en leer la respuesta del Banco Agrario = " + timeRead + " [Tiempo maximo configurado = " + readTimeout + " ].");
            logISOMsg(isoMsg);
        } else {
            log.error("El Socket se ha desconectado");
        }
        return isoMsg;
    }

}



