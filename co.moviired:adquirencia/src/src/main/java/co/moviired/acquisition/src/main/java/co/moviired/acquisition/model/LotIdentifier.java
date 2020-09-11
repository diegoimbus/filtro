package co.moviired.acquisition.model;

import java.util.Date;

@SuppressWarnings("unused")
public interface LotIdentifier {

    String getLotIdentifier();

    Date getCreationDate();

    String getProductIdentifier();

    Long getCodesCount();
}

