package co.moviired.moneytransfer.manager.notifier;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.moneytransfer.client.notifier.SmsData;
import co.moviired.moneytransfer.client.notifier.SmsRequest;
import co.moviired.moneytransfer.client.notifier.SmsResponse;
import co.moviired.moneytransfer.domain.model.dto.PersonDTO;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.helper.ConstanHelper;
import co.moviired.moneytransfer.helper.UtilHelper;
import co.moviired.moneytransfer.properties.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Slf4j
@Service
public class SmsNotifier implements INotifier {

    private final ReactiveConnector supportSMSClient;
    private final SmsProperties smsProperties;

    public SmsNotifier(@NotNull @Qualifier("supportSmsClient") ReactiveConnector supportSMSClient, SmsProperties pSmsProperties) {
        this.supportSMSClient = supportSMSClient;
        this.smsProperties = pSmsProperties;
    }

    @Override
    public void notify(MoneyTransferRequest moneyTransferRequest, PersonDTO personDTO) {

        // Armar el mensaje
        SmsData data = SmsData.builder()
                .phoneNumber(moneyTransferRequest.getPhoneNumberSender())
                .messageContent(personDTO.getTypePerson().equals(ConstanHelper.ORIGINATOR)?smsProperties.getSmsContentOriginator():smsProperties.getSmsContentBeneficiary())
                .build();

        SmsRequest sms = new SmsRequest(data);

        log.info("Request ws SmsNotifier :{}", sms.toString());

            // Enviar el mensaje
            this.supportSMSClient.post(sms, SmsResponse.class, MediaType.APPLICATION_JSON, new HashMap<>())
                    .flatMap(notifyReponse ->{

                        UtilHelper.assignCorrelative(moneyTransferRequest.getCorrelationId());

                        SmsResponse response = (SmsResponse) notifyReponse;
                        log.info("Response ws SmsNotifier :{}", response.toString());
                        return Mono.just("ok");

                    })
                    .doOnTerminate(() -> log.info("SMS enviado correctamente, al teléfono: {}", moneyTransferRequest.getPhoneNumberSender()))
                    .onErrorResume(e -> {
                        log.error("Error sms:{}",e.getMessage());
                        return Mono.just("Error sms");
                    }).subscribe();

    }

}

