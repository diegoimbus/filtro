package co.moviired.moneytransfer.domain.entity.redshift;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "giro_financiero_giro_flete", schema = "proyectos_moviired")
public class GiroFlete {

    @Id
    @Column(name = "id_giro_flete")
    private Integer idGiroFlete;

    @Column(name = "desde")
    private String desde;

    @Column(name = "hasta")
    private String hasta;

    @Column(name = "movii_financiero_sin_iva")
    private Double tarifa;

    @Column(name = "movii_financiero_con_iva")
    private Double tarifaIva;

}

