package co.moviired.mahindrafacade.domain.proccesor;

import co.moviired.mahindrafacade.client.mahindra.MahindraConnector;
import co.moviired.mahindrafacade.client.mahindra.Request;
import co.moviired.mahindrafacade.client.mahindra.Response;
import co.moviired.mahindrafacade.domain.entity.mahindrafacade.User;
import co.moviired.mahindrafacade.domain.enums.OperationType;
import co.moviired.mahindrafacade.domain.provider.IProccesor;
import co.moviired.mahindrafacade.domain.repository.mahindrafacade.IUserRepository;
import co.moviired.mahindrafacade.helper.ConstantsHelper;
import co.moviired.mahindrafacade.helper.UtilsHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static co.moviired.mahindrafacade.helper.ConstantsHelper.TRANSACTION_200;


@Slf4j
@Component
@AllArgsConstructor
public final class UserRequestProccesor implements IProccesor {

    private final IUserRepository userRepository;
    private final MahindraConnector mahindraConnectorClient;

    @Override
    public Mono<String> proccess(Request rqCommand, OperationType typeResponse, String correlationId) {

        // 1.Buscar en la BD
        return userRepository.findByMsisdn(rqCommand.getMsisdn())
                .flatMap(userMongo -> {

                    UtilsHelper.assignCorrelative(correlationId);
                    log.info("SI existe registro con id: {}", userMongo.getId());

                    Response response = Response.parse(userMongo);
                    return Mono.just(UtilsHelper.parseToXml(transformer(response, rqCommand, typeResponse)));
                })
                .switchIfEmpty(Mono.defer(() -> mahindraConnectorClient.sendMahindraRequest(rqCommand, correlationId)
                        // 2.Si no está en BD, login contra MAHINDRA, y guardar en BD
                        .flatMap(response -> {
                            UtilsHelper.assignCorrelative(correlationId);

                            // Verificar exitoso MH para guardar en BD
                            if (TRANSACTION_200.equals(response.getTxnstatus())) {
                                User user = User.parse(response);
                                userRepository.save(user).subscribe();
                                log.info("NO existe registro, usuario guardado mongoDB");
                            }

                            return Mono.just(UtilsHelper.parseToXml(response));
                        })));
    }

    private Response transformer(Response response, Request request, OperationType typeResponse) {

        response.setType(typeResponse.name());
        if (OperationType.USRQRYINFORES.equals(typeResponse)) {

            if( !request.getUsertype().equals(response.getUsertype()) ){
                return generateErrorUserType();
            }

            response.setUserid(null);
            response.setFirstname(null);
            response.setLastname(null);
            response.setProvider(null);
            response.setLangcode(null);
            response.setGrade(null);
            response.setTcp(null);
            response.setWalletnumber(null);
            response.setIdtype(null);
            response.setIdno(null);
            response.setBirthplace(null);
            response.setSecurityquestionsflag(null);
            response.setEmail(null);
            response.setLastlogin(null);
            response.setExempted(null);
            response.setTxnstatusmf(null);
            response.setMessagemf(null);
            response.setAgentcode(null);
            response.setUsertype(null);

            if (response.getGender().equals(ConstantsHelper.GEN_MAL)) {
                response.setGender("Male");
            } else if (response.getGender().equals(ConstantsHelper.GEN_FEM)) {
                response.setGender("Female");
            } else if (response.getDob().contains(ConstantsHelper.DOB_COMPARE)) {
                try {
                    response.setDob(new SimpleDateFormat("ddMMyyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(response.getDob().split(" ")[0])));
                } catch (ParseException e) {
                    log.error("Error: {}", e.getMessage());
                }
            }

        } else if (OperationType.AUTHPINRESP.equals(typeResponse)) {
            response.setStatus(null);
            response.setTxnstatusmf(null);
            response.setMessagemf(null);
            response.setAgentcode(null);
            response.setFname(null);
            response.setLname(null);
            response.setCity(null);
            response.setBarredtype(null);
            response.setPreflanguage(null);
            response.setEmailid(null);
        }

        return response;
    }

    private Response generateErrorUserType(){
        Response response = new Response();
        response.setType(ConstantsHelper.ERROR_TYPE);
        response.setTxnstatus(ConstantsHelper.ERROR_TXN_STATUS);
        response.setMessage(ConstantsHelper.ERROR_MESSAGE);
        return response;
    }

}
