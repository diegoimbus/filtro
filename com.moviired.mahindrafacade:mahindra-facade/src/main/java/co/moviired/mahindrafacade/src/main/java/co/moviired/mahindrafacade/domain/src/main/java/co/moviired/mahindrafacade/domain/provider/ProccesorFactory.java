package co.moviired.mahindrafacade.domain.provider;

import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.mahindrafacade.domain.enums.OperationType;
import co.moviired.mahindrafacade.domain.proccesor.UserRequestProccesor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@AllArgsConstructor
public class ProccesorFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final UserRequestProccesor userRequestProccesor;

    public final IProccesor getProccesor(OperationType operationType) throws ProcessingException {
        IProccesor proccesor;

        switch (operationType) {
            case AUTHPINREQ:
            case USRQRYINFO:
                proccesor = this.userRequestProccesor;
                break;

            default:
                throw new ProcessingException();
        }

        return proccesor;
    }
}


