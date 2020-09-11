package co.moviired.moneytransfer.helper;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentType implements Serializable {
    private String alias;
    private String descripcion;

}

