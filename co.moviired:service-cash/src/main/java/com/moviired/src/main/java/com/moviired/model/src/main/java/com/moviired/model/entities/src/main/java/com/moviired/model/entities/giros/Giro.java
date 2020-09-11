package com.moviired.model.entities.giros;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static com.moviired.helper.Constant.FIFTEEN;

@Data
@Entity
@Table(name = "GIRO")
public class Giro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "giro_id", length = FIFTEEN, nullable = false)
    private Integer giroId;

    @Column(name = "giro_login_origen")
    private String loginOrigen;

    @Column(name = "giro_origen")
    private String origen;

    @Column(name = "giro_destino")
    private String giroDestino = "0";

    @Column(name = "giro_monto")
    private Integer amount;

    @Column(name = "giro_pago")
    private String giroPago;

    @Column(name = "giro_envio")
    private String envio;

    @Column(name = "giro_descripcion")
    private String giroDescripcion = "";

    @Column(name = "giro_fechapago")
    private Date fechaPago;

    @Column(name = "giro_fecharegistro")
    private Date fechaRegistro = new Date();

    @Column(name = "giro_fechaenvio")
    private Date fechaEnvio = new Date();

    @Column(name = "giro_registradopor")
    private String registradoPor;

    @Column(name = "giro_flete")
    private Integer flete;

    @Column(name = "factura_envio")
    private String facturaEnvio;

    @Column(name = "respuesta_envio")
    private String respuestaEnvio = "00";

    @Column(name = "sendsms")
    private String sendSMS = "0";

    @Column(name = "smsvalue")
    private String smsValue = "0";

    @Column(name = "idoperator")
    private String idOperator = "0";

    @Column(name = "dist_origen")
    private String distOrigen;

    @Column(name = "dist_destino")
    private String distDestino = "0";

    @Column(name = "pingiro")
    private String pinGiro;

    @Column(name = "estadogiro_id")
    private Integer estadoId;

    @Column(name = "cadena_destino")
    private String cadenaDestino;

    @Column(name = "cadena_origen")
    private String cadenaOrigen;

    @Column(name = "GIRO_TPID_REM")
    private String tPidRem;

    @Column(name = "GIRO_TPIDNOMBRE_REM")
    private String tPidNombreRem;

    @Column(name = "GIRO_NUMEROIDENTIFICACION_REM")
    private String numIdentificacionRem;

    @Column(name = "GIRO_NOMBRE_REM")
    private String nombreRem;

    @Column(name = "GIRO_APELLIDO_REM")
    private String apellidoRem;

    @Column(name = "GIRO_DIRECCION_REM")
    private String giroDireccionRem = "";

    @Column(name = "GIRO_DEPARTAMENTO_REM")
    private String giroDepartamentoRem = "";

    @Column(name = "GIRO_CIUDAD_REM")
    private String giroCiudadRem = "";

    @Column(name = "GIRO_TELEFONO_REM")
    private String giroTelefonoRem = "0";

    @Column(name = "GIRO_ZONAID_DEST")
    private String giroZonaIdDest = "0";

    @Column(name = "GIRO_DIRECCION_DEST")
    private String giroDireccionDest = "";

    @Column(name = "GIRO_DEPARTAMENTO_DEST")
    private String giroDepartamentoDest = "";

    @Column(name = "GIRO_CIUDAD_DEST")
    private String giroCiudadDest = "";

    @Column(name = "GIRO_TELEFONO_DEST")
    private String giroTelefonoDest = "";

    @Column(name = "GIRO_CELULAR_REM")
    private String celularRem;

    @Column(name = "GIRO_TPID_DEST")
    private String giroTpidDest = "1";

    @Column(name = "GIRO_TPIDNOMBRE_DEST")
    private String giroTpidNombreDest;

    @Column(name = "GIRO_NUMEROIDENTIFICACION_DEST")
    private String giroNumeroIdentificacionDest;

    @Column(name = "GIRO_NOMBRE_DEST")
    private String giroNombreDest;

    @Column(name = "GIRO_APELLIDO_DEST")
    private String giroApellidoDest;

    @Column(name = "GIRO_CELULAR_DEST")
    private String giroCelularDest;

    @Column(name = "PTO_NOMBRE_DEST")
    private String ptoNombreDest;

    @Column(name = "PTO_DEPARTAMENTO_DEST")
    private String ptoDepartamentoDest = "";

    @Column(name = "PTO_DIRECCION_DEST")
    private String ptoDireccionDest = "";

    @Column(name = "PTO_BARRIO_DEST")
    private String ptoBarrioDest = "";

    @Column(name = "PTO_TELEFONO_DEST")
    private String ptoTelefonoDest = "";

    @Column(name = "PTO_CIUDAD_DEST")
    private String ptoCiudadDest = "";

    @Column(name = "PTO_NOMBRE_ORG")
    private String ptoNombreOrg;

    @Column(name = "PTO_DEPARTAMENTO_ORG")
    private String ptoDeptoOrg;

    @Column(name = "PTO_CIUDAD_ORG")
    private String ptoCiudadOrg;

    @Column(name = "PTO_DIRECCION_ORG")
    private String ptoDireccionOrg;

    @Column(name = "PTO_BARRIO_ORG")
    private String ptoBarrioOrg;

    @Column(name = "PTO_TELEFONO_ORG")
    private String ptoTelefonoOrg;

    @Column(name = "GIRO_REFERENCIA")
    private String referencia;

    @Column(name = "SWITCHID_ENVIO")
    private String switchIdEnvio;

    @Column(name = "HUELLA_ENVIO")
    private String huellaEnvio = "";

}

