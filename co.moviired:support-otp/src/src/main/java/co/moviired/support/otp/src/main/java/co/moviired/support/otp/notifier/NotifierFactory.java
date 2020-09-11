package co.moviired.support.otp.notifier;

import co.moviired.support.otp.exception.InvalidNotifierException;
import co.moviired.support.otp.model.enums.NotifyChannel;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Service
public class NotifierFactory implements Serializable {

    private final INotifier smsNotifier;
    private final INotifier callNotifier;
    private final INotifier emailNotifier;

    public NotifierFactory(
            @NotNull INotifier smsNotifier,
            @NotNull INotifier emailNotifier,
            @NotNull INotifier callNotifier) {
        super();
        this.smsNotifier = smsNotifier;
        this.emailNotifier = emailNotifier;
        this.callNotifier = callNotifier;
    }

    public INotifier get(NotifyChannel type) throws InvalidNotifierException {
        INotifier notifier;
        switch (type) {
            case SMS:
                notifier = this.smsNotifier;
                break;

            case CALL:
                notifier = this.callNotifier;
                break;

            case EMAIL:
                notifier = this.emailNotifier;
                break;

            default:
                throw new InvalidNotifierException("");
        }

        return notifier;
    }

}

