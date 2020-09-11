package co.moviired.moneytransfer.domain.entity.redshift;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "giro_financiero_destinatario", schema = "proyectos_moviired")
public class GiroFinancieroBeneficiario {

    @Id
    @Column(name = "celular")
    private String celular;

    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "anio")
    private Integer anio;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "cant_trx_mayor_tope")
    private Integer cantTrxMayorTope;

    @Column(name = "cant_trx_menor_tope")
    private Integer cantTrxMenorTope;

}

