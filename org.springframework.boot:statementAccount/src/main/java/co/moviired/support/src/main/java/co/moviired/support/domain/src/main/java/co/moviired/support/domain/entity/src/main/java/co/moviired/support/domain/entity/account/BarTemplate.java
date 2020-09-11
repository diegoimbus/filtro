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
@Table(name = "bar_template")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BarTemplate implements Serializable {

    private static final long serialVersionUID = 527701445404960468L;
    private static final int LENGTH = 15;
    private static final int LENGTH_50 = 50;
    private static final int LENGTH_10 = 10;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = LENGTH, nullable = false)
    private Integer id;

    @Column(name = "name", length = LENGTH_50, nullable = false)
    private String name;

    @Column(name = "bar_type", length = LENGTH_10, nullable = false, unique = true)
    private String barType;

    @Column(name = "enabled_template", nullable = false)
    private Boolean enabledTemplate;

    @Column(name = "enabled_unbar", nullable = false)
    private Boolean enabledUnbar;

    @Column(name = "bar_limit_amount", nullable = false)
    private Integer barLimitAmount;

    @Column(name = "bar_time", length = LENGTH_10, nullable = false)
    private String barTime;

    @Column(name = "bar_day_monday", nullable = false)
    private Boolean barDayMonday;

    @Column(name = "bar_day_tuesday", nullable = false)
    private Boolean barDayTuesday;

    @Column(name = "bar_day_wednesday", nullable = false)
    private Boolean barDayWednesday;

    @Column(name = "bar_day_thursday", nullable = false)
    private Boolean barDayThursday;

    @Column(name = "bar_day_friday", nullable = false)
    private Boolean barDayFriday;

}

