package co.moviired.topups.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class OperatorStatusSearchProperties implements Serializable {

    @Value("#{'${spring.application.operator.status.getOperatorsStatus:1,2}'.split(',')}")
    private List<Integer> operatorStatus;

    @Value("${spring.application.operator.status.operatorsBlockSize}")
    private int blockSize;
}

