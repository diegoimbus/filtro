package co.moviired.topups.model.domain.dto;

import co.moviired.topups.model.domain.dto.recharge.IGestorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestorId implements IGestorId {

    private static final long serialVersionUID = 1L;

    private String prefix;

    private String name;

    private String mappedValue;

    private String operatorName;

}

