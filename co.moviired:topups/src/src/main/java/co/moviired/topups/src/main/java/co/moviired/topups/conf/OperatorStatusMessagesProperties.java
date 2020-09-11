package co.moviired.topups.conf;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class OperatorStatusMessagesProperties implements Serializable {

    @Value(value = "${spring.messages.inactiveOperator}")
    public String operatorStatusInactiveMsg;

    @Value(value = "${spring.messages.suspendedOperator}")
    public String operatorStatusSuspendedMsg;
}

