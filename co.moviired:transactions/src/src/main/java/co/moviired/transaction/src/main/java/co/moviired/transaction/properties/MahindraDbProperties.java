package co.moviired.transaction.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class MahindraDbProperties implements Serializable {

    private static final long serialVersionUID = 9192455177850672025L;

    @Value("${properties.connectionMahindraDB.schema}")
    private String schema;

    @Value("${properties.connectionMahindraDB.schema2}")
    private String schema2;

}

