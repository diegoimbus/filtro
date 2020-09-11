package co.moviired.transpiler.integration.iso.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsoClient implements Serializable {

    private static final long serialVersionUID = 7353787682700892273L;

    private String name;
    private Integer port;
    private String packager;

}

