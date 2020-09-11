package co.moviired.support.domain.dto;

import co.moviired.support.domain.entity.account.BarTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @version 1.0.7
 * @category consignment
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class BarTemplateDTO implements Serializable {

    private static final long serialVersionUID = 527701445404960468L;

    private Integer id;

    private String name;

    private String barType;

    private Boolean enabledTemplate;

    private Boolean enabledUnbar;

    private Integer barLimitAmount;

    private String barTime;

    private Boolean barDayMonday;

    private Boolean barDayTuesday;

    private Boolean barDayWednesday;

    private Boolean barDayThursday;

    private Boolean barDayFriday;

    public BarTemplate toEntity() {
        BarTemplate barTemplate = new BarTemplate();
        barTemplate.setId(this.id);
        barTemplate.setName(this.name);
        barTemplate.setBarType(this.barType);
        barTemplate.setEnabledTemplate(this.enabledTemplate);
        barTemplate.setEnabledUnbar(this.enabledUnbar);
        barTemplate.setBarLimitAmount(this.barLimitAmount);
        barTemplate.setBarTime(this.barTime);
        barTemplate.setBarDayMonday(this.barDayMonday);
        barTemplate.setBarDayTuesday(this.barDayTuesday);
        barTemplate.setBarDayWednesday(this.barDayWednesday);
        barTemplate.setBarDayThursday(this.barDayThursday);
        barTemplate.setBarDayFriday(this.barDayFriday);

        return barTemplate;
    }
}

