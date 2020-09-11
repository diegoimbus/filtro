//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.moviired.client;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import com.moviired.helper.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public abstract class ISOPersistentClient implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ISOPersistentClient.class);
    private Socket socket;
    private InputStream entrada;
    private OutputStream salida;
    private boolean serverActive;
    private String socketIp;
    private int socketPort;
    private int connectionTimeout;
    private int readTimeOut;

    public ISOPersistentClient(String pSocketIP, int pSocketPort, int pConnectionTimeOut, int pReadTimeOut) {
        this.setSocketProperties(pSocketIP, pSocketPort, pConnectionTimeOut, pReadTimeOut);
    }

    public abstract byte[] commuter(String input);

    /**
     * metodo setSocketPropertis
     *
     * @param pSocketIp,pSocketPort,pConnectionTimeOut,pReadTimeOut
     * @return void
     */
    public void setSocketProperties(String pSocketIp, int pSocketPort, int pConnectionTimeOut, int pReadTimeOut) {
        this.socketIp = pSocketIp;
        this.socketPort = pSocketPort;
        this.connectionTimeout = pConnectionTimeOut;
        this.readTimeOut = pReadTimeOut;
    }

    /**
     * metodo run
     */
    public void run() {
        try {
            this.serverStart();

            do {
                this.verifySocketAlive();
                this.serverAccept();
            } while (this.serverActive);
        } catch (ServiceException | Exception var5) {
            LOGGER.error("ISO PERSISTENT CLIENT: ERROR. Cause: {}", var5.getMessage());
        } finally {
            this.serverStop();
        }

    }

    private void serverStart() throws SocketException {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(this.socketIp, this.socketPort), this.connectionTimeout);
            this.socket.setSoTimeout(this.readTimeOut);
            this.socket.setKeepAlive(Boolean.TRUE);
            this.socket.setTcpNoDelay(Boolean.TRUE);
            this.entrada = this.socket.getInputStream();
            this.salida = this.socket.getOutputStream();
            this.serverActive = Boolean.TRUE;
            LOGGER.info("**** ISO PERSISTENT CLIENT: Iniciado correctamente! ****");
        } catch (IOException var3) {
            String message = "ISO PERSISTENT CLIENT: Error al abrir el puerto: " + var3.getMessage();
            LOGGER.error(message, var3);
            this.serverActive = Boolean.FALSE;
            throw new SocketException(message);
        }
    }

    private void serverStop() {
        try {
            if (this.salida != null) {
                this.salida.close();
            }

            if (this.entrada != null) {
                this.entrada.close();
            }

            if (this.socket != null) {
                this.socket.close();
            }

            LOGGER.debug("********* ISO PERSISTENT CLIENT: Detenido correctamente! **********");
            this.serverActive = Boolean.FALSE;
        } catch (IOException var2) {
            LOGGER.error(var2.getMessage());
        }

    }

    private void serverAccept() throws ServiceException {
        LOGGER.debug("ISO PERSISTENT CLIENT: Esperando petición...");

        try {
            String trama = this.getRequestData(this.entrada, 0, false);
            if (trama == null || trama.isEmpty()) {
                return;
            }

            new Thread(() -> {
                try {
                    byte[] response = this.commuter(trama);
                    String resp = new String(response);
                    this.salida.write(this.addTramaLength(resp));
                    this.salida.write('\n');
                    this.salida.flush();
                } catch (Exception var4) {
                    LOGGER.error("ISO PERSISTENT CLIENT: Error procesando petición. Causa: {}", var4.getMessage());
                }

            }).start();
        } catch (Exception var2) {
            LOGGER.error("ISO PERSISTENT CLIENT: Error leyendo petición. Causa: {}", var2.getMessage());
        }

    }

    private String getRequestData(InputStream pEntrada, int longitudParam, boolean processTrama) throws ServiceException {
        byte[] sizeByte = new byte[1];
        byte[] hexa = new byte[2];
        StringBuilder trama = new StringBuilder();
        int longitud = longitudParam;

        try {
            if (!processTrama) {
                if (pEntrada.read(hexa) == -1) {
                    throw new ServiceException(ErrorType.COMMUNICATION, "-1", "no se obtuvo conexión con el proveedor");
                } else {
                    longitud = this.transformDecimal(hexa) - 2;
                    return this.getRequestData(this.entrada, longitud, true);
                }
            } else {
                while (trama.length() < longitud) {
                    if (pEntrada.read(sizeByte) != -1) {
                        trama.append(new String(sizeByte, StandardCharsets.UTF_8));
                    }
                }

                trama.trimToSize();
                return trama.toString();
            }
        } catch (SocketTimeoutException var8) {
            LOGGER.debug(var8.getMessage());
            return trama.toString();
        } catch (Exception var9) {
            LOGGER.error(var9.getMessage());
            throw new ServiceException(ErrorType.COMMUNICATION, "-1", "no se obtuvo conexión con el proveedor");
        }
    }

    private void verifySocketAlive() throws SocketException {
        try {
            if (this.socket.isClosed() || !this.socket.isBound() || !this.socket.isConnected()) {
                LOGGER.debug("ISO PERSISTENT CLIENT: Cerrado.");
                this.serverStart();
            }
        } catch (Exception var2) {
            LOGGER.debug("ISO PERSISTENT CLIENT: Cerrado.");
            this.serverStart();
        }

    }

    private byte[] addTramaLength(String outputLine) {
        int totalLength = outputLine.length() + 2;
        byte[] lengthByte = new byte[]{(byte) (totalLength >> Constant.SIZE_BYTE & Constant.TWO_HUNDRED_AND_FIFTY_FIVE), (byte) (totalLength & Constant.TWO_HUNDRED_AND_FIFTY_FIVE)};

        try {
            byte[] temp = outputLine.getBytes(StandardCharsets.ISO_8859_1);
            String slong = new String(lengthByte) + new String(temp);
            return slong.getBytes();
        } catch (Exception var6) {
            LOGGER.error("Error escribiendo a socket {}", var6.getMessage());
            return outputLine.getBytes();
        }
    }

    private int transformDecimal(@NotNull byte[] lengthByte) {
        int length = 0;

        try {
            length = (lengthByte[0] << Constant.SIZE_BYTE) + (lengthByte[1] & Constant.TWO_HUNDRED_AND_FIFTY_FIVE);
        } catch (Exception var4) {
            LOGGER.error("Error leyendo la longitud en bytes. Cause: {}", var4.getMessage());
            LOGGER.info("jeiner diaz");
        }

        return length;
    }
}

