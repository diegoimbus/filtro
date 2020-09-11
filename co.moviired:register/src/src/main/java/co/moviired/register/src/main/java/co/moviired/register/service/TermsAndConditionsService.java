package co.moviired.register.service;

import co.moviired.base.domain.exception.DataException;
import co.moviired.register.domain.dto.RegisterResponse;
import co.moviired.register.helper.UtilsHelper;
import co.moviired.register.properties.TermsAndConditionsProperties;
import co.moviired.register.providers.termsandconditions.Data;
import co.moviired.register.providers.termsandconditions.Request;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedHashMap;

@Slf4j
@Service
@SuppressWarnings("unchecked")
public final class TermsAndConditionsService implements Serializable {

    private final TermsAndConditionsProperties termsAndConditionsProperties;

    public TermsAndConditionsService(@NotNull TermsAndConditionsProperties pTermsAndConditionsProperties) {
        super();
        this.termsAndConditionsProperties = pTermsAndConditionsProperties;
    }

    private Mono<String> loginApiTermsConditions() {
        return Mono.just("").flatMap(param -> {
            JsonObject request = new JsonObject();
            request.addProperty("username", termsAndConditionsProperties.getUser());
            request.addProperty("password", termsAndConditionsProperties.getPassword());
            ResponseEntity<Data> entity = (ResponseEntity<Data>) UtilsHelper.consume(termsAndConditionsProperties.getUrlLogin(), request, Data.class, null, null);
            log.info("entity " + entity.getHeaders().getFirst("Authorization"));
            return Mono.just(entity.getHeaders().getFirst("Authorization"));
        });

    }

    public Mono<RegisterResponse> registrarTermsAndConditions(String documentNumber, Boolean param1, Boolean param2, Boolean param3) {
        return Mono.just(documentNumber)
                .flatMap(idoNo -> loginApiTermsConditions())
                .flatMap(token -> {
                    RegisterResponse registerResponse = new RegisterResponse();
                    try {
                        // 1- login y generar token.
                        // 2- validar terminos y condiciones
                        Request request = new Request();
                        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                        data.put("origen", "ORQUESTADOR");
                        data.put("term1", param1);
                        data.put("term2", param2);
                        data.put("term3", param3);
                        data.put("document", documentNumber);
                        data.put("empresa", "Orquestador Registraduria");
                        request.setData(data);
                        ResponseEntity<String> entity = (ResponseEntity<String>) UtilsHelper.consume(termsAndConditionsProperties.getUrlIntocheckandpersoninto(), request, String.class, token, null);
                        if (entity.getBody().contains("\"code\":\"00\"")) {
                            registerResponse.setCode("00");
                            registerResponse.setMessage("Exito");
                            return Mono.just(registerResponse);
                        }

                        throw new DataException("1", "No se pudo registrar sus terminos y condiciones, No se puede registrar, Intente mas tarde");

                    } catch (DataException e) {
                        registerResponse.setCode("99");
                        registerResponse.setMessage("Fallido");
                        return Mono.just(registerResponse);
                    }
                });
    }
}

