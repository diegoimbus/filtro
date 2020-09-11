package co.moviired.auth.server.helper;

import co.moviired.auth.server.domain.dto.ResponseGoogle;
import co.moviired.auth.server.properties.GlobalProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Recaptcha V3 - Java Example
 */
@Slf4j
public final class GoogleRecaptcha {

    private static final double RECAPTCHA_VALID_RESULT = 0.5;

    private final ObjectMapper objectMapper;
    private final GlobalProperties googleProperties;

    public GoogleRecaptcha(ObjectMapper pobjectMapper, GlobalProperties pgoogleProperties) {
        this.objectMapper = pobjectMapper;
        this.googleProperties = pgoogleProperties;
    }

    public boolean isValid(String clientRecaptchaResponse) throws IOException {
        if (clientRecaptchaResponse == null || "".equals(clientRecaptchaResponse)) {
            return false;
        }

        URL obj = new URL(googleProperties.getUrlGoogle());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //add client result as post parameter
        String postParams = "secret=" + googleProperties.getSecretGoogle() + "&response=" + clientRecaptchaResponse;

        // send post request to google recaptcha server
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(postParams);
        }
        int responseCode = con.getResponseCode();
        log.info("Response Code: " + responseCode);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            log.info(response.toString());

            ResponseGoogle json = this.objectMapper.readValue(response.toString(), ResponseGoogle.class);
            log.info("success : " + json.isSuccess());
            log.info("score : " + json.getScore());

            //result should be sucessfull and spam score above 0.5
            return (json.isSuccess() && json.getScore() >= RECAPTCHA_VALID_RESULT);
        }
    }
}

