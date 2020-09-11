package co.moviired.transaction.model.convenios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ws_operator")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
public class Operator implements Serializable {

    private static final long serialVersionUID = 5984936939799451582L;

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "ean_code")
    private String eanCode;

    @Column(name = "name")
    private String name;

    @Column(name = "product_code")
    private String productCode;

}

