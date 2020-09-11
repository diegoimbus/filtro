package co.moviired.supportp2pvalidatortransaction.common.model.network;

import co.moviired.supportp2pvalidatortransaction.common.model.IModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.EMPTY_STRING;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest<D extends IModel> {

    @Builder.Default
    private HttpMethod httpMethod = HttpMethod.POST;
    @Builder.Default
    private MediaType mediaType = MediaType.APPLICATION_JSON;
    @Builder.Default
    private String path = EMPTY_STRING;
    private D body;
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();
}

