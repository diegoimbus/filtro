package co.moviired.register.domain.factory.clevertap;

import co.moviired.register.domain.dto.ClevertapDTO;
import co.moviired.register.domain.model.clevertap.ClevertapEvent;

import java.util.ArrayList;
import java.util.List;

public final class ClevertapDTOHelper {

    private ClevertapDTOHelper() {
        super();
    }

    public static ClevertapDTO getUploadEventRequest(List<ClevertapEvent> clevertapEvents) {
        ClevertapDTO clevertapDTO = new ClevertapDTO();
        clevertapDTO.setD(clevertapEvents);
        return clevertapDTO;
    }

    public static ClevertapDTO getUploadEventRequest(ClevertapEvent clevertapEvent) {
        ClevertapDTO clevertapDTO = new ClevertapDTO();
        clevertapDTO.setD(new ArrayList<>());
        clevertapDTO.getD().add(clevertapEvent);
        return clevertapDTO;
    }

}

