package com.moviired.repository.impl;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CommandHelper;
import co.moviired.connector.connector.ReactiveConnector;
import com.moviired.client.mahindra.command.Request;
import com.moviired.client.mahindra.command.Response;
import com.moviired.conf.StatusCodeConfig;
import com.moviired.excepciones.ManagerException;
import com.moviired.helper.Constant;
import com.moviired.repository.ICashRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

import static co.moviired.base.helper.CommandHelper.printIgnore;


/*
 * Copyright @2018. MOVIIRED. Todos los derechos reservados.
 *
 * @author Cuadros Cerpa, Cristian Alfonso
 * @version 1, 2018-06-20
 * @since 1.0
 */
@AllArgsConstructor
@Slf4j
@Data
@Service
public class CashRepository implements ICashRepository {

    private static final String LBLREQUEST = "Request enviado a Mahindra: ";
    private static final String LBLRESPONSE = "Respuesta de Mahindra: ";
    private static final long serialVersionUID = 227535084565096536L;

    private final StatusCodeConfig statusCodeConfig;
    private final ReactiveConnector mahindraClient;

    // Procesa los datos recibidos
    private Response processResponse(String responseBody) throws IOException {
        return CommandHelper.readXML(responseBody, Response.class);
    }

    private Document convertStringToDocument(String xmlStr) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlStr)));

    }

    /**
     * metodo sendMahindraRequest (Envia peticion COMMAND a mahindra).
     *
     * @param request
     * @return Response
     */
    @Override
    public Response sendMahindraRequest(Request request) throws ServiceException {
        try {
            String command = CommandHelper.writeAsXML(request);
            log.info("Request MH: {}", printIgnore(command, "MPIN", "PIN"));

            String response = (String) this.mahindraClient.post(command, String.class, MediaType.APPLICATION_XML, null).block();

            if ((response.trim().isEmpty())) {
                throw new ServiceException(ErrorType.COMMUNICATION, "500", "No se obtuvo respuesta desde Mahindra.");
            }

            log.info("Response MH: {}", printIgnore(response, "MPIN", "PIN"));

            return processResponse(response);

        } catch (Exception e) {
            throw new ServiceException(ErrorType.COMMUNICATION, "500", "No pudimos conectarnos con el servidor. Por favor vuelve a intentarlo.");
        }
    }

    @Override
    public final Response sendRequestPending(String request, String phoneNumber) throws IOException, SAXException, ParserConfigurationException {
        String response = (String) this.mahindraClient.post(request, String.class, MediaType.APPLICATION_XML, null).block();

        log.info(LBLRESPONSE + response);

        Document document = convertStringToDocument(response);
        if (document == null) {
            throw new ManagerException(Constant.NUMBER_TWO_NEGATIVE_EXCEPTION, "400", "Error en la respuesta de mahindra: Document = null.");
        }

        Element docEle = document.getDocumentElement();
        NodeList nodeList = docEle.getChildNodes();
        Node n;
        Element eElement;
        int k = 0;
        String amount = "0";
        String id = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                eElement = (Element) nodeList.item(i);
                if (eElement.getNodeName().contains("TRANSDETAILS") && eElement.getElementsByTagName("FROM").item(0).getTextContent().matches(phoneNumber)) {
                    k++;
                    amount = eElement.getElementsByTagName("TXNAMT").item(0).getTextContent();
                    id = eElement.getElementsByTagName("TXNID").item(0).getTextContent();
                    break;
                }
            }
        }

        Response responseData = processResponse(response);
        if (k == 0) {
            responseData.setTxnstatus("400");
            responseData.setMessage("No hay transacciones para mostrar");
        } else {
            responseData.setRespuesta(amount);
            responseData.setTxnid(id);
        }

        return responseData;

    }
}

