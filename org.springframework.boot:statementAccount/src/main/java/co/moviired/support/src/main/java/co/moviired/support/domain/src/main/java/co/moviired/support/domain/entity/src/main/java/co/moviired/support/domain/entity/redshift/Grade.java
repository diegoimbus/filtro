package co.moviired.support.domain.entity.redshift;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estado_cuenta_merchant_grado", schema = "proyectos_moviired")
public class Grade implements Serializable {
    @Id
    @Column(name = "id_grado")
    private Integer id;

    @Column(name = "grado")
    private String grado;
}

