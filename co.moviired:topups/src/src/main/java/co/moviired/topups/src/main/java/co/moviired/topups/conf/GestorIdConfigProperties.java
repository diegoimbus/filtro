package co.moviired.topups.conf;

import co.moviired.topups.model.domain.dto.GestorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "gestorid")
public class GestorIdConfigProperties implements Serializable {

    private Map<String, List<GestorId>> operators = new HashMap<>();

}

