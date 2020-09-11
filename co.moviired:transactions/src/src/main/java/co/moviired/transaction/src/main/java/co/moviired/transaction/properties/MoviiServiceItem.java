package co.moviired.transaction.properties;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MoviiServiceItem implements Serializable {
    private static final long serialVersionUID = 5657932475535664420L;
    private String code;
    private String name;
}

