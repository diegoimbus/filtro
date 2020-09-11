package co.moviired.support.endpoint.util.generics;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.HeaderAuthentication;
import co.moviired.support.endpoint.util.util.ConsignmentUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateAuthentication {

    private final ConsignmentUtilities consignmentUtilities;

    public ValidateAuthentication(ConsignmentUtilities consignmentUtilities) {
        super();
        this.consignmentUtilities = consignmentUtilities;
    }


    public boolean validate(HeaderAuthentication headerAuthentication) throws ParsingException {
        boolean validate = false;


        String userName = headerAuthentication.getUserName();
        String password = headerAuthentication.getPass();

        if(userName.equals(consignmentUtilities.loadPropertyBogota("userBogota")) && password.equals(consignmentUtilities.loadPropertyBogota("passBogota"))){
            validate = true;
        }

        return validate;
    }
}


