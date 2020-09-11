package co.moviired.register.helper.schedulerhelper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerHelperDTO implements Serializable {
    private Long nextTime;
    private boolean isSuccess;
}
