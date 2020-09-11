package co.moviired.business.helper;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.Builder;

@Slf4j
public class SignatureBuilder implements Builder<String> {

    private final StringBuilder buffer;

    public SignatureBuilder() {
        super();
        buffer = new StringBuilder();
    }

    public SignatureBuilder append(final Object dato) throws JsonProcessingException {
        this.buffer.append(new ObjectMapper().writer().writeValueAsString(dato));
        return this;
    }

    public SignatureBuilder append(final String valor) {
        this.buffer.append(valor);
        return this;
    }

    @Override
    public String build() {
        buffer.trimToSize();
        return DigestUtils.sha1Hex(buffer.toString());
    }

}

