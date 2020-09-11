package co.moviired.support.otp.notifier;

import co.moviired.support.otp.model.entity.Otp;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface INotifier extends Serializable {

    void notify(@NotNull String uuid, @NotNull Otp otp);

}

