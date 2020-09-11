package co.moviired.transaction.model.convenios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ws_biller")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
public class Biller implements Serializable {

    private static final long serialVersionUID = -2391049125889765628L;

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "biller_code")
    private String billerCode;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "ean_code")
    private String eanCode;

    @Column(name = "name")
    private String name;

}

