package co.moviired.transpiler;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IsoTests {

    private static final String hostname = "localhost";
    private static final int port = 7000;

    @Test
    public void trama_SiEnterOK() {
        final String responseOK = "07d02100200000006000002121015152710000800084100008|0|00|TRANSACCIÓN EXITOSA|14|0000000000000000|000|2.0562924E7|000000|0|0|00000";
        final String trama = "0BE02007220000128E0100204000200001600000010000010010006474383850983011125723770717532280|312439475900035243838512345678    707403     9311112558               BOGOTA       CO    707403      000\n";

        try (Socket socket = new Socket(hostname, port)) {
            // Enviar la petición
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output);
            writer.println(trama);
            writer.flush();

            // Leer la respuesta
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String response = reader.readLine();

            // Verficar la respuesta
            assertThat(response).isEqualTo(responseOK);

        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Test
    public void trama_NoEnterOK() {
        final String responseOK = "07d02100200000006000002121015152710000800084100008|0|00|TRANSACCIÓN EXITOSA|14|0000000000000000|000|2.0562924E7|000000|0|0|00000";
        final String trama = "0BE02007220000128E0100204000200001600000010000010010006474383850983011125723770717532280|312439475900035243838512345678    707403     9311112558               BOGOTA       CO    707403      000";

        try (Socket socket = new Socket(hostname, port)) {
            // Enviar la petición
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output);
            writer.println(trama);
            writer.flush();

            // Leer la respuesta
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String response = reader.readLine();

            // Verficar la respuesta
            assertThat(response).isEqualTo(responseOK);

        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Test
    public void trama_lengthIncompleto() {
        final String responseOK = "trama-invalida";
        final String trama = "0C00200";

        try (Socket socket = new Socket(hostname, port)) {
            // Enviar la petición
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output);
            writer.println(trama);
            writer.flush();

            // Leer la respuesta
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String response = reader.readLine();

            // Verficar la respuesta
            assertThat(response).isEqualTo(responseOK);

        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Test
    public void trama_Incompleta() {
        final String responseOK = "trama-invalida";
        final String trama = "0BE02007220000128E0100204000200001600000010000010010006474383850983011125723770717532280|312439475900035243838512345678    707403     9311112558               BOGOTA       CO    707403";

        try (Socket socket = new Socket(hostname, port)) {
            // Enviar la petición
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output);
            writer.println(trama);
            writer.flush();

            // Leer la respuesta
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String response = reader.readLine();

            // Verficar la respuesta
            assertThat(response).isEqualTo(responseOK);

        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}

