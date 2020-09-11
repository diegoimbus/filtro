package co.moviired.support.domain.entity.redshift;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@Entity
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "extractos_clientes_merchant", schema = "proyectos_moviired")
public class ExtractData {

    @Id
    @Column(name = "celular")
    private String phoneNumber;
    @Column(name = "nombre_producto")
    private String productName;
    @Column(name = "cantidad")
    private Integer count;
    @Column(name = "movilizado")
    private Double total;
    @Column(name = "saldo_inicial")
    private Double initialBalance;
    @Column(name = "saldo_final")
    private Double endBalance;
    @Column(name = "anio")
    private Integer year;
    @Column(name = "mes_no")
    private Integer month;
    @Column(name = "nombre_punto")
    private String storeName;
    @Column(name = "nombre_rl")
    private String userName;
    @Column(name = "cedula")
    private String documentNumber;

    public ExtractData(Integer pyear, Integer pmonth) {
        this.year = pyear;
        this.month = pmonth;
    }

    public ExtractData(String pphoneNumber,
                       String pproductName,
                       Integer pcount,
                       Double ptotal,
                       Double pinitialBalance,
                       Double pendBalance,
                       Integer pyear,
                       Integer pmonth,
                       String pstoreName,
                       String puserName,
                       String pdocumentNumber) {
        this.phoneNumber = pphoneNumber;
        this.productName = pproductName;
        this.count = pcount;
        this.total = ptotal;
        this.initialBalance = pinitialBalance;
        this.endBalance = pendBalance;
        this.year = pyear;
        this.month = pmonth;
        this.storeName = pstoreName;
        this.userName = puserName;
        this.documentNumber = pdocumentNumber;
    }
}

