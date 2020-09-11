package com.moviired.helper;

import com.moviired.model.response.AtallaResponse;
import com.moviired.properties.AtallaProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.moviired.helper.Constant.*;

/**
 * @author: Jeiner Diaz
 * @version: 01/06/2020/
 */

@Slf4j
public class AtallaHelper {

    private final String host;
    private final int port;
    private final int timeout;
    private final AtallaProperties atallaProperties;

    public AtallaHelper(String pHost, int pPort, int pTimeOut, AtallaProperties pAtallaProperties) {
        this.host = pHost;
        this.port = pPort;
        this.timeout = pTimeOut;
        this.atallaProperties = pAtallaProperties;
    }

    // GENERATE REQUESTS ***********************************************************************************************

    private String getGenerateMacRequest(String message) {
        return atallaProperties.getMessageGenerateMac()
                .replace(atallaProperties.getDataLengthPlaceHolder(), String.valueOf(message.length()))
                .replace(atallaProperties.getDataPlaceHolder(), message);
    }

    private String getValidateRequest(String message, String mac) {
        return atallaProperties.getMessageValidateMac()
                .replace(atallaProperties.getDataLengthPlaceHolder(), String.valueOf(message.length()))
                .replace(atallaProperties.getDataPlaceHolder(), message)
                .replace(atallaProperties.getMacPlaceHolder(), mac);
    }

    // VALIDATE RESPONSES **********************************************************************************************

    private AtallaResponse mapGenerateResponse(String response) {
        AtallaResponse atallaResponse = validateMessage(response, atallaProperties.getRegexGenerateMessageResponse());
        if (atallaResponse.isStatus()) {
            String[] data = getResponseValues(response);
            atallaResponse.setMac(data[2]);
            atallaResponse.setVerificationDigits(data[THREE]);
        }
        return atallaResponse;
    }

    private AtallaResponse mapValidateResponse(String response) {
        AtallaResponse atallaResponse = validateMessage(response, atallaProperties.getRegexValidateMessageResponse());
        if (atallaResponse.isStatus()) {
            String[] data = getResponseValues(response);
            atallaResponse.setValid(data[2].equals(YES));
            atallaResponse.setVerificationDigits(data[THREE]);
        }
        return atallaResponse;
    }

    private AtallaResponse validateMessage(String response, String regex) {
        AtallaResponse atallaResponse = new AtallaResponse();
        if (response.matches(regex)) {
            atallaResponse.setStatus(true);
            atallaResponse.setValidMessage(true);
            return atallaResponse;
        }

        atallaResponse.setStatus(false);
        if (response.matches(atallaProperties.getRegexErrorMessageResponse())) {
            String[] data = getResponseValues(response);
            atallaResponse.setAtallaStatus(data[0]);
            atallaResponse.setErrorCode(data[1].substring(0, 2));
            atallaResponse.setErrorLocation(data[1].substring(2, FOUR));
            atallaResponse.setError(data[1]);
            atallaResponse.setValidMessage(true);
        } else {
            atallaResponse.setValidMessage(false);
        }
        return atallaResponse;
    }

    private String[] getResponseValues(String response) {
        return response.replace(SMALLER_THAN, EMPTY_STRING).replace(GREATER_THAN, EMPTY_STRING).split(NUMERAL);
    }


    public final AtallaResponse sendMessage(boolean requestType, String trama, String mac) throws IOException {
        String request;
        if (requestType) {
            request = getGenerateMacRequest(trama);
        } else {
            request = getValidateRequest(trama, mac);
        }
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), Boolean.TRUE);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            log.info("REQUEST ATALLA : {} ", request);
            socket.setSoTimeout(timeout);
            out.println(request);
            out.flush();
            String response = in.readLine();
            if (response == null) {
                return null;
            }
            AtallaResponse atallaResponse;

            if (requestType) {
                atallaResponse = mapGenerateResponse(response);
            } else {
                atallaResponse = mapValidateResponse(response);
            }
            log.info("RESPONSE ATALLA : {} ", response);
            return atallaResponse;
        }
    }
}

