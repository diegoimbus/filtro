package co.moviired.moneytransfer.domain.validations;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.moneytransfer.client.blacklist.BlackListRequest;
import co.moviired.moneytransfer.client.registraduria.RegisterRequest;
import co.moviired.moneytransfer.client.supportathentication.SupportAuthenticationRequest;
import co.moviired.moneytransfer.domain.entity.redshift.GiroFlete;
import co.moviired.moneytransfer.domain.entity.redshift.Header;
import co.moviired.moneytransfer.domain.enums.OperationType;
import co.moviired.moneytransfer.domain.model.dto.PersonDTO;
import co.moviired.moneytransfer.domain.model.request.MoneyTransferRequest;
import co.moviired.moneytransfer.domain.model.response.MoneyTransferResponse;
import co.moviired.moneytransfer.domain.repository.redshift.IGiroFinancieroBeneficiarioRepository;
import co.moviired.moneytransfer.domain.repository.redshift.IGiroFinancieroRemitenteRepository;
import co.moviired.moneytransfer.domain.repository.redshift.IGiroFlete;
import co.moviired.moneytransfer.domain.repository.redshift.IHeaderRepository;
import co.moviired.moneytransfer.helper.ConstanHelper;
import co.moviired.moneytransfer.manager.blacklist.IBlackList;
import co.moviired.moneytransfer.manager.notifier.INotifier;
import co.moviired.moneytransfer.manager.registraduria.IRegistraduria;
import co.moviired.moneytransfer.manager.supportauthentication.ISupportAuthentication;
import co.moviired.moneytransfer.properties.GlobalProperties;
import co.moviired.moneytransfer.properties.StatusCodeConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class Validation implements Serializable {

    private static final long serialVersionUID = -8407978670018357216L;

    private final IRegistraduria registraduria;
    private final IBlackList blackList;
    private final ISupportAuthentication supportAuthentication;
    private final GlobalProperties globalProperties;
    private final IGiroFinancieroBeneficiarioRepository giroFinancieroBeneficiarioRepository;
    private final IGiroFinancieroRemitenteRepository giroFinancieroRemitenteRepository;
    private final IGiroFlete giroFleteRepository;
    private final IHeaderRepository headerRepository;
    private final StatusCodeConfig statusCodeConfig;
    private final INotifier notifier;

    public Mono<MoneyTransferResponse> validationRegistry(MoneyTransferRequest moneyTransferRequest, PersonDTO personDTO) {

        RegisterRequest request = new RegisterRequest();
        request.setIdentificationType(personDTO.getDocumentType());
        request.setDocumentNumber(personDTO.getDocumentNumber());

        return registraduria.searchRegistraduria(request, moneyTransferRequest).flatMap(response -> {

            if (ConstanHelper.SUCCESS_CODE_0.equals(response.getStatusDTO().getCode())) {

                personDTO.setName((response.getFirstName() + " " + response.getSecondName() + " " + response.getFirstSurname() + " " + response.getSecondSurname()).replace(" null", ""));

                if (personDTO.getTypePerson().equals(ConstanHelper.ORIGINATOR)) {
                    moneyTransferRequest.setNameSender(personDTO.getName());
                } else if (personDTO.getTypePerson().equals(ConstanHelper.BENEFICIARY)) {
                    moneyTransferRequest.setNameReceiver(personDTO.getName());
                }

                return Mono.just(new MoneyTransferResponse(response, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "Exitoso, SI existe en registraduria"));
            } else {
                return Mono.error(new ServiceException(ErrorType.DATA, statusCodeConfig.of(ConstanHelper.ERROR_NOT_EXIST_REGISTRY, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(ConstanHelper.ERROR_NOT_EXIST_REGISTRY, StatusCode.Level.FAIL).getMessage()));
            }
        });

    }

    public Mono<MoneyTransferResponse> validationBlackList(MoneyTransferRequest moneyTransferRequest, PersonDTO personDTO) {

        BlackListRequest request = new BlackListRequest();
        request.setDocumentNumber(personDTO.getDocumentNumber());
        request.setUserName(personDTO.getName());

        return blackList.searchBlackList(request, moneyTransferRequest).flatMap(response -> {

            if (StatusCode.Level.SUCCESS.value().equals(response.getCode()) && response.getItem().getBlock().equals("false")) {
                return Mono.just(new MoneyTransferResponse(response, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "Exitoso blacklist, No esta reportado en listas"));
            } else {
                return Mono.error(new ServiceException(ErrorType.DATA, statusCodeConfig.of(ConstanHelper.ERROR_BLACK_LIST, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(ConstanHelper.ERROR_BLACK_LIST, StatusCode.Level.FAIL).getMessage()));
            }

        });

    }

    public Mono<MoneyTransferResponse> validationClientMovii(MoneyTransferRequest moneyTransferRequest, PersonDTO personDTO) {

        SupportAuthenticationRequest authenticationRequest = new SupportAuthenticationRequest();
        authenticationRequest.setCorrelationId(moneyTransferRequest.getCorrelationId());
        authenticationRequest.setUserType(globalProperties.getUserTypeAuth());
        authenticationRequest.setUserLogin(personDTO.getPhoneNumber());

        return supportAuthentication.userQueryInfo(authenticationRequest, moneyTransferRequest).flatMap(response -> {

            if (StatusCode.Level.SUCCESS.value().equals(response.getErrorCode())) {
                return Mono.just(new MoneyTransferResponse(response, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "Exitoso MH, SI es cliente MOVII"));
            } else {
                return Mono.just(new MoneyTransferResponse(null, ConstanHelper.ERROR_TYPE_DATA, statusCodeConfig.of(ConstanHelper.ERROR_NOT_CLIENT_MOVII, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(ConstanHelper.ERROR_NOT_CLIENT_MOVII, StatusCode.Level.FAIL).getMessage()));
            }
        });

    }

    public Mono<MoneyTransferResponse> validationTop(MoneyTransferRequest moneyTransferRequest, PersonDTO personDTO) {

        boolean applyTop = false;

        log.info("Inicia validacion tope:{}", personDTO.getTypePerson());

        //Valida trimestre, semestre y anual
        if (personDTO.getTypePerson().equals(ConstanHelper.ORIGINATOR)) {

            if (giroFinancieroRemitenteRepository.findCountTop(personDTO.getDocumentType(), personDTO.getDocumentNumber(), personDTO.getPhoneNumber(), globalProperties.getQuarterly()) >= globalProperties.getTxnQuarterly()) {
                applyTop = true;
            } else if (giroFinancieroRemitenteRepository.findCountTop(personDTO.getDocumentType(), personDTO.getDocumentNumber(), personDTO.getPhoneNumber(), globalProperties.getBiannual()) >= globalProperties.getTxnBiannual()) {
                applyTop = true;
            } else if (giroFinancieroRemitenteRepository.findCountTop(personDTO.getDocumentType(), personDTO.getDocumentNumber(), personDTO.getPhoneNumber(), globalProperties.getAnnual()) >= globalProperties.getTxnAnnual()) {
                applyTop = true;
            } else {
                log.info("No aplica validacion tope:{}", personDTO.getTypePerson());
            }

        } else {//BENEFICIARY

            if (giroFinancieroBeneficiarioRepository.findCountTop(personDTO.getDocumentType(), personDTO.getDocumentNumber(), personDTO.getPhoneNumber(), globalProperties.getQuarterly()) >= globalProperties.getTxnQuarterly()) {
                applyTop = true;
            } else if (giroFinancieroBeneficiarioRepository.findCountTop(personDTO.getDocumentType(), personDTO.getDocumentNumber(), personDTO.getPhoneNumber(), globalProperties.getBiannual()) >= globalProperties.getTxnBiannual()) {
                applyTop = true;
            } else if (giroFinancieroBeneficiarioRepository.findCountTop(personDTO.getDocumentType(), personDTO.getDocumentNumber(), personDTO.getPhoneNumber(), globalProperties.getAnnual()) >= globalProperties.getTxnAnnual()) {
                applyTop = true;
            } else {
                log.info("No aplica validacion tope:{}", personDTO.getTypePerson());
            }

        }

        log.info("Finaliza validacion tope para:{} - aplica tope:{}", personDTO.getTypePerson(), applyTop);

        if (!applyTop) {

            return Mono.just(new MoneyTransferResponse(moneyTransferRequest, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "Exitoso topes, NO aplica topes"));
        } else {
            notifier.notify(moneyTransferRequest, personDTO);
            return Mono.error(new ServiceException(ErrorType.DATA, statusCodeConfig.of(ConstanHelper.EXCEEDS_STOPS, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(ConstanHelper.EXCEEDS_STOPS, StatusCode.Level.FAIL).getMessage()));
        }

    }

    public Mono<MoneyTransferResponse> validationFreight(MoneyTransferRequest moneyTransferRequest) {

        log.info("Inicia validacion flete");
        GiroFlete giroFlete = giroFleteRepository.findByTarifaBetween(moneyTransferRequest.getAmount());
        log.info("Finaliza validacion flete");

        if (giroFlete != null && giroFlete.getTarifa() != null) {

            moneyTransferRequest.setFreight(calculateFreight(giroFlete.getTarifa(), moneyTransferRequest.getAmount()));
            moneyTransferRequest.setFreightIva(calculateFreight(giroFlete.getTarifaIva(), moneyTransferRequest.getAmount()));
            moneyTransferRequest.setAmountTotal(calculateTotal(moneyTransferRequest));

            return Mono.just(new MoneyTransferResponse(moneyTransferRequest, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "Exitoso flete"));
        } else {
            return Mono.error(new ServiceException(ErrorType.DATA, statusCodeConfig.of(ConstanHelper.ERROR_FREIGHT, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(ConstanHelper.ERROR_FREIGHT, StatusCode.Level.FAIL).getMessage()));
        }
    }

    private Integer calculateFreight(Double freight, Integer pAmount) {
        Double amount = Double.parseDouble(String.valueOf(pAmount));
        return (int) (Math.round(freight < 1 ? freight * amount : freight));
    }

    private Integer calculateTotal(MoneyTransferRequest moneyTransferRequest) {
        return moneyTransferRequest.getAmount() + (moneyTransferRequest.getFreightIva());
    }

    public void validateInput(MoneyTransferRequest request, String autorization, OperationType opType) throws DataException {

        validateAuthorization(request, autorization);

        if (request.getSource() == null) {
            throw new DataException("10", "Source es un parámetro obligatorio");
        }

        if(!request.getSource().equals(globalProperties.getTypeSource())){
            throw new DataException("10", "El valor del source es invalido");
        }

        if (request.getIdTypeSender() == null && !(opType.equals(OperationType.PAY_GIRO) || opType.equals(OperationType.RESEND_OTP))) {
            throw new DataException("10", "idTypeSender es un parámetro obligatorio");
        }

        if (request.getIdSender() == null && !(opType.equals(OperationType.PAY_GIRO) || opType.equals(OperationType.RESEND_OTP))) {
            throw new DataException("10", "idSender es un parámetro obligatorio");
        }

        if (request.getIdTypeReceiver() == null && !(opType.equals(OperationType.RESEND_OTP) || opType.equals(OperationType.CANCEL_GIRO_PLACE_GIRO))) {
            throw new DataException("10", "idTypeReceiver es un parámetro obligatorio");
        }

        if (request.getIdReceiver() == null && !(opType.equals(OperationType.RESEND_OTP) || opType.equals(OperationType.CANCEL_GIRO_PLACE_GIRO))) {
            throw new DataException("10", "idReceiver es un parámetro obligatorio");
        }

        if (request.getIdSender() != null && request.getIdReceiver() != null && request.getIdSender().equals(request.getIdReceiver())) {
            throw new DataException("10", "idSender NO puede ser igual a idReceiver");
        }

        if (request.getMoneyTransferId() == null && opType.equals(OperationType.RESEND_OTP)) {
            throw new DataException("10", "moneyTransferId es un parámetro obligatorio");
        }

        if (request.getTransactionId() == null && opType.equals(OperationType.RESEND_OTP)) {
            throw new DataException("10", "transactionId es un parámetro obligatorio");
        }

        if (request.getDetailCancel() == null && opType.equals(OperationType.CANCEL_GIRO_PLACE_GIRO) ) {
            throw new DataException("10", "detailCancel es un parámetro obligatorio");
        }

        if (!(opType.equals(OperationType.LIST_PENDING_GIRO) || opType.equals(OperationType.RESEND_OTP))) {

            if (request.getEanCode() == null) {
                throw new DataException("10", "eanCode es un parámetro obligatorio");
            }

            if (request.getIssuerDate() == null) {
                throw new DataException("10", "issuerDate es un parámetro obligatorio");
            }

            if (request.getOrigin() == null) {
                throw new DataException("10", "origin es un parámetro obligatorio");
            }

            if (request.getPhoneNumberSender() == null && !opType.equals(OperationType.PAY_GIRO)) {
                throw new DataException("10", "phoneNumberSender es un parámetro obligatorio");
            }

            if (request.getPhoneNumberReceiver() == null && !opType.equals(OperationType.CANCEL_GIRO_PLACE_GIRO)) {
                throw new DataException("10", "phoneNumberReceiver es un parámetro obligatorio");
            }

            if (request.getPhoneNumberSender() != null && request.getPhoneNumberReceiver() != null && request.getPhoneNumberSender().equals(request.getPhoneNumberReceiver())) {
                throw new DataException("10", "phoneNumberSender NO puede ser igual a phoneNumberReceiver");
            }

            if (request.getTransferId() == null && opType.equals(OperationType.CANCEL_GIRO_PLACE_GIRO)) {
                throw new DataException("10", "transferId es un parámetro obligatorio");
            }

            if (request.getAmount() == null && !(opType.equals(OperationType.CANCEL_GIRO) || opType.equals(OperationType.PAY_GIRO))) {
                throw new DataException("10", "amount es un parámetro obligatorio");
            }

            if(request.getAmount() != null && request.getAmount() > globalProperties.getTopAmount() && !(opType.equals(OperationType.CANCEL_GIRO) || opType.equals(OperationType.PAY_GIRO))){
                throw new DataException("10", "el monto maximo para amount es "+ globalProperties.getTopAmount());
            }

            if (request.getOtp() == null && (opType.equals(OperationType.PAY_GIRO) || opType.equals(OperationType.CANCEL_GIRO))) {
                throw new DataException("10", "otp es un parámetro obligatorio");
            }
        }

    }

    private void validateAuthorization(MoneyTransferRequest request, String autorization) throws DataException {

        if (!autorization.trim().matches("")) {

            String[] vautorization = autorization.split(":");

            if (!vautorization[0].trim().matches("") && !vautorization[1].trim().matches("")) {
                request.setUser(vautorization[0]);
            } else {
                throw new DataException("10", "Error header autorization");
            }

        } else {
            throw new DataException("10", "Error header autorization");
        }

    }

    public Mono<MoneyTransferResponse> getTransaction(MoneyTransferRequest moneyTransferRequest) {

        Optional<Header> txn = headerRepository.findById(moneyTransferRequest.getTransferId());

        if (txn.isPresent()){
            moneyTransferRequest.setUserMerchant(txn.get().getRemarks().split(ConstanHelper.REGEXP_7)[0]);//Cel del punto donde se realizo giro
            return Mono.just(new MoneyTransferResponse(moneyTransferRequest, ConstanHelper.SUCCESS_CODE_0, StatusCode.Level.SUCCESS.value(), "Exitoso txn"));
        } else {
            return Mono.error(new ServiceException(ErrorType.DATA, statusCodeConfig.of(ConstanHelper.ERROR_TXN, StatusCode.Level.FAIL).getCode(), statusCodeConfig.of(ConstanHelper.ERROR_TXN, StatusCode.Level.FAIL).getMessage()));
        }

    }
}

