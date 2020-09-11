package co.moviired.business.domain.jpa.movii.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class JsonItemField implements Serializable {

    private static final long serialVersionUID = 91987923423436L;

    private String alias;
    private String type;
    private String format;
    private String label;
    private String placeHolder;
    private String title;
    private int min;
    private int max;
    private boolean visible;

}

