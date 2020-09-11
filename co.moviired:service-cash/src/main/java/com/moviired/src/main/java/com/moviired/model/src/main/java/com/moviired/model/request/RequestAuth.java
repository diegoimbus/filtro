package com.moviired.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 * @category srv-cash
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAuth implements Serializable {

    private static final long serialVersionUID = 3014706042455506811L;
    private String authorization;
    private String correlationId;
}

