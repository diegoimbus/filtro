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
@Table(name = "barUnbar_config")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class BarUnbarConfig implements Serializable {

    private static final long serialVersionUID = 527701445404960468L;
    private static final int LENGTH = 15;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = LENGTH, nullable = false)
    private Integer id;

    @Column(name = "enabled_bar", nullable = false)
    private Boolean enabledBar;
    @Column(name = "enabled_unbar", nullable = false)
    private Boolean enabledUnbar;

    @Column(name = "running_bar", nullable = false)
    private Boolean runningBar;
    @Column(name = "running_unbar", nullable = false)
    private Boolean runningUnbar;

    public Integer getId() {
        return id;
    }

    public Boolean getEnabledBar() {
        return enabledBar;
    }


    public Boolean getEnabledUnbar() {
        return enabledUnbar;
    }


    public Boolean getRunningBar() {
        return runningBar;
    }

    public void setRunningBar(Boolean prunningBar) {
        this.runningBar = prunningBar;
    }

    public Boolean getRunningUnbar() {
        return runningUnbar;
    }

    public void setRunningUnbar(Boolean prunningUnbar) {
        this.runningUnbar = prunningUnbar;
    }
}

