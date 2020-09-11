package co.moviired.digitalcontent.incomm.repository;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.io.IOException;
import java.io.Serializable;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-01-24
 * @since 1.0
 */
public interface IIncommRepository extends Serializable {

    ISOMsg sendRequest(ISOMsg sendMsg) throws ISOException, IOException;

    void setLoggerHiddenField(String[] hideFields);
}

