package com.moviired.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class GiroProperties implements Serializable {

    @Value("${giros.properties.login-origen}")
    private String loginOrigen;

    @Value("${giros.properties.origen}")
    private String origen;

    @Value("${giros.properties.dist-origen}")
    private String distOrigen;

    @Value("${giros.properties.cadena-origen}")
    private String cadenaOrigen;

    @Value("${giros.properties.cadena-destino}")
    private String cadenaDestino;

    @Value("${giros.properties.rem.tpid}")
    private String remTpid;

    @Value("${giros.properties.rem.tpid-nombre}")
    private String remTpidName;

    @Value("${giros.properties.rem.num-ident}")
    private String remNumIdent;

    @Value("${giros.properties.rem.nombre}")
    private String remNombre;

    @Value("${giros.properties.rem.apellido}")
    private String remApellido;

    @Value("${giros.properties.rem.celular}")
    private String remCelular;

    @Value("${giros.properties.org.nombre}")
    private String orgNombre;

    @Value("${giros.properties.org.departamento}")
    private String orgDepto;

    @Value("${giros.properties.org.ciudad}")
    private String orgCiudad;

    @Value("${giros.properties.org.direccion}")
    private String orgDireccion;

    @Value("${giros.properties.org.barrio}")
    private String orgBarrio;

    @Value("${giros.properties.org.telefono}")
    private String orgTelefono;

}

