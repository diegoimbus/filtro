package com.moviired.manager;

import com.moviired.excepciones.ManagerException;
import com.moviired.model.Network;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Service
public class OtpManagerFactory implements Serializable {

    private static final long serialVersionUID = 5732741808030346065L;
    private transient AvalOtpManager avalOtpManager;
    private transient InternalOtpManager internalOtpManager;

    public OtpManagerFactory(
            @NotNull AvalOtpManager pAvalOtpManager,
            @NotNull InternalOtpManager pInternalOtpManger) {
        super();
        this.avalOtpManager = pAvalOtpManager;
        this.internalOtpManager = pInternalOtpManger;
    }

    /**
     * metodo getOtpManager (Identificador de redManager generador de OTP (interno o externo))
     *
     * @param red
     * @return IOtpManager
     */
    public IOtpManager getOtpManager(@NotNull Network.GeneratorOTP red) throws ManagerException {
        IOtpManager manager;

        switch (red) {
            case AVAL:
                manager = this.avalOtpManager;
                break;

            case INTERNAL:
                manager = this.internalOtpManager;
                break;

            default:
                throw new ManagerException(0, "-001", "La red proporcionada es inválida");
        }

        return manager;
    }

}

