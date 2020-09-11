package co.moviired.moneytransfer.domain.entity.redshift;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "giro_financiero_remitente", schema = "proyectos_moviired")
public class GiroFinancieroRemitente {

    @Id
    @Column(name = "celular")
    private String celularRemitente;

    @Column(name = "tipo_documento")
    private String tipoDocumentoRemitente;

    @Column(name = "numero_documento")
    private String numeroDocumentoRemitente;

    @Column(name = "fecha")
    private String fechaRemitente;

    @Column(name = "anio")
    private Integer anioRemitente;

    @Column(name = "mes")
    private Integer mesRemitente;

    @Column(name = "cant_trx_mayor_tope")
    private Integer cantTrxMayorTopeRemitente;

    @Column(name = "cant_trx_menor_tope")
    private Integer cantTrxMenorTopeRemitente;

}

