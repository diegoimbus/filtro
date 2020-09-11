package co.moviired.supportp2pvalidatortransaction.common.model.method;

import lombok.Data;

@Data
public abstract class IMethod {
    private String name;
    private Boolean isEnable;
}

