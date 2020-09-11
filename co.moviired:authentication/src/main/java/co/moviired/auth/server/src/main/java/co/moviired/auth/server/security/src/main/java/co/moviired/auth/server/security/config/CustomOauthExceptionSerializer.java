package co.moviired.auth.server.security.config;

import co.moviired.auth.server.exception.CustomOauthException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public final class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {
    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("errorCode", value.getMessage().split(":")[0]);
        gen.writeStringField("errorMessage", value.getMessage().split(":")[1]);
        gen.writeEndObject();
    }
}

