package co.moviired.support.domain.entity.redshift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estado_cuenta_merchant_historico", schema = "proyectos_moviired")

public class StatementAccounts implements Serializable {
    @Id
    @Column(name = "celular")
    private String celular;

    @Column(name = "saldo")
    private Double saldo;

    @Column(name = "cupo")
    private Double cupo;

    @Column(name = "valor_minimo_pagar")
    private Double valorMinimoPagar;

    @Column(name = "pago_total")
    private Double pagoTotal;

    @Column(name = "comision_mensual")
    private Double comisionMensual;

    @Column(name = "comision_diaria")
    private Double comisionDiaria;

    @Column(name = "estado_cartera")
    private String estadoCartera;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_bloqueo")
    private Date fechaBloqueo;

    @Column(name = "tipo_bloqueo")
    private String tipoBloqueo;

    @Column(name = "cashin")
    private Double cashIn;

    @Column(name = "cashout")
    private Double cashOut;

}

