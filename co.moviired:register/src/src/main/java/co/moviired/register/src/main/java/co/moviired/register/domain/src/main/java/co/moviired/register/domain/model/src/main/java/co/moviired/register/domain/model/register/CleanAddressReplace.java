package co.moviired.register.domain.model.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CleanAddressReplace implements Serializable {
    private static final long serialVersionUID = -6498309817262719675L;

    private String initValue;
    private String finalValue;
}

