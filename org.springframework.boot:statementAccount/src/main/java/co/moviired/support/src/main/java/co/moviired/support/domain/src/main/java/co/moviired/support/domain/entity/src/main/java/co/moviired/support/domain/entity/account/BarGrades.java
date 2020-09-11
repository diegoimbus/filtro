package co.moviired.support.domain.entity.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @version 1.0.7
 * @category consignment
 */

@Entity
@Data
@Table(name = "bar_grades")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BarGrades implements Serializable {

    private static final long serialVersionUID = 527701445404960468L;
    private static final int LENGTH = 15;
    private static final int LENGTH_NAME = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = LENGTH, nullable = false)
    private Integer id;

    @Column(name = "name", length = LENGTH_NAME, nullable = false)
    private String name;

}

