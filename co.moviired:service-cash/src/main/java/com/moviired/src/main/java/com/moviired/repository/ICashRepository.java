package com.moviired.repository;

import co.moviired.base.domain.exception.ServiceException;
import com.moviired.client.mahindra.command.Request;
import com.moviired.client.mahindra.command.Response;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;

/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
public interface ICashRepository extends Serializable {

    Response sendMahindraRequest(Request request) throws ServiceException;

    Response sendRequestPending(String request, String phoneNumber) throws IOException, SAXException, ParserConfigurationException, JAXBException;

}

