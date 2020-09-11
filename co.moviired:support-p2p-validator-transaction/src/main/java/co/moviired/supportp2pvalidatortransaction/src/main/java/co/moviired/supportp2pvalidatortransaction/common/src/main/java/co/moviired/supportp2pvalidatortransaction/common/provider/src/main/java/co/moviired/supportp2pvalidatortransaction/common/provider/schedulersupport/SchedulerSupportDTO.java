package co.moviired.supportp2pvalidatortransaction.common.provider.schedulersupport;

import co.moviired.supportp2pvalidatortransaction.common.model.dto.IComponentDTO;
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
