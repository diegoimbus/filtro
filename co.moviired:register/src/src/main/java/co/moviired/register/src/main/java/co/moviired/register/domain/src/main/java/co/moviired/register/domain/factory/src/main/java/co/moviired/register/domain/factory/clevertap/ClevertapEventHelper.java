package co.moviired.register.domain.factory.clevertap;

import co.moviired.register.config.PropertiesHandler;
import co.moviired.register.domain.enums.ado.AdoCaseStatus;
import co.moviired.register.domain.enums.clevertap.Event;
import co.moviired.register.domain.enums.register.Status;
import co.moviired.register.domain.model.clevertap.ClevertapEvent;
import co.moviired.register.domain.model.clevertap.ClevertapEventData;

import java.util.Date;

import static co.moviired.register.helper.ConstantsHelper.MILLIS_IN_ONE_SECOND;

public final class ClevertapEventHelper {

    private ClevertapEventHelper() {
        super();
    }

    public static ClevertapEvent getClevertapEvent(Event event,
                                                   String identity,
                                                   Integer adoId,
                                                   Integer serviceId,
                                                   Integer adoStatus,
                                                   Integer statusId,
                                                   PropertiesHandler propertiesHandler) {
        ClevertapEvent clevertapEvent = new ClevertapEvent();
        clevertapEvent.setIdentity(identity);
        clevertapEvent.setEvtName(event.getName(propertiesHandler.getClevertapProperties()));
        clevertapEvent.setType(propertiesHandler.getClevertapProperties().getEventType());
        clevertapEvent.setTs(String.valueOf(new Date().getTime() / MILLIS_IN_ONE_SECOND));

        ClevertapEventData clevertapEventData = new ClevertapEventData();
        clevertapEventData.setOrigin(propertiesHandler.getGlobalProperties().getName());
        clevertapEventData.setVersion(propertiesHandler.getGlobalProperties().getVersion());
        clevertapEventData.setAdoTransactionId(adoId);
        clevertapEventData.setAdoStatusName(AdoCaseStatus.getById(adoStatus).name());
        clevertapEventData.setServiceRegistrationId(serviceId);
        clevertapEventData.setStatus(Status.getById(statusId).name());
        clevertapEventData.setAdoStatusId(adoStatus);
        clevertapEvent.setEvtData(clevertapEventData);

        return clevertapEvent;
    }
}

