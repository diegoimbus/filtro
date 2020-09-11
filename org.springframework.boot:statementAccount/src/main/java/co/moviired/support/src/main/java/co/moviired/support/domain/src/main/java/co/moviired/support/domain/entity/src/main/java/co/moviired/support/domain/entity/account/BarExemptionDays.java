package co.moviired.support.domain.entity.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @version 1.0.7
 * @category consignment
 */

@Entity
@Data
@Table(name = "bar_exemption_days")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BarExemptionDays implements Serializable {

    private static final long serialVersionUID = 527701445404960468L;
    private static final int LENGTH = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = LENGTH, nullable = false)
    private Integer id;

    @Column(name = "day", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar day;

}

