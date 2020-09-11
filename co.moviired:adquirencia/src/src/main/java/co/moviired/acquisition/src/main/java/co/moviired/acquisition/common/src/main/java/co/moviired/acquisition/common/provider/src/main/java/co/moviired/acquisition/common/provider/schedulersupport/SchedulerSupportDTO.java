package co.moviired.acquisition.common.provider.schedulersupport;

import co.moviired.acquisition.common.model.dto.IComponentDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchedulerSupportDTO extends IComponentDTO {

    private String process;
    private Long minimumTimeBetweenInstances;

    private Long nextTime;
}
