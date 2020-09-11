package com.moviired.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class BankDetailDTO implements Serializable {

    private static final long serialVersionUID = -5818221489111278213L;

    private String id;

    private String name;
}

