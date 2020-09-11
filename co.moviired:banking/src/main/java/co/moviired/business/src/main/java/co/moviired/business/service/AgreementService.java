package co.moviired.business.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.CommunicationException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.enums.*;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import co.moviired.business.domain.jpa.movii.repository.IBillerRepository;
import co.moviired.business.helper.BillBusinessRules;
import co.moviired.business.helper.UtilHelper;
import co.moviired.business.properties.BankingProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AgreementService {

    private static final String SPLIT_PIPE = "\\|";
    private static final String NOT_EXIT_CODE = "NOT EXIT CODE INTERNAL";

    private final StatusCodeConfig statusCodeConfig;
    private final IBillerRepository iBillerRepository;
    private final BillBusinessRules billBusinessRules;
    private final BankingProperties bankingProperties;

    // Consulta la información de convenios para proceso de facturas
    public void findAgreementByProccessBill(RequestFormatBanking bankingRequest, OperationType opType) throws ServiceException {
        if ((opType.equals(OperationType.QUERY_BILL) || opType.equals(OperationType.PAY_BILL))) {
            Biller biller;
            if (CollectionType.MANUAL.equals(bankingRequest.getTypePayBill())) {
                biller = iBillerRepository.getByBillerCode(bankingRequest.getServiceCode());
            } else if (opType.equals(OperationType.PAY_BILL)) {
                executeRuleTypePaymentAllowed(bankingRequest);
                biller = iBillerRepository.getByEanCode(bankingRequest.getEan13BillerCode());
            } else {
                biller = iBillerRepository.getByEanCode(bankingRequest.getReferenceNumber().substring(3, 16));
            }

            bankingRequest.setModality(Modality.ONLINE);
            bankingRequest.setWeftType(WeftType.GENERIC);
            bankingRequest.setInternalCode(NOT_EXIT_CODE);

            if (biller != null) {
                specialAgreements(opType, bankingRequest, biller);
                billBusinessRules.executeRuleCollectionType(bankingRequest, biller);
                bankingRequest.setModality(biller.getModality());
                bankingRequest.setBillerName((biller.getName() == null) ? "" : biller.getName());
                bankingRequest.setPartialPayment(biller.getPartialPayment() != null && biller.getPartialPayment());
                validateConfigBiller(bankingRequest, opType, biller);
            }
        }
    }

    // Validaciones para convenios BEPS, PAYNET y SOAT
    private void specialAgreements(OperationType opType, RequestFormatBanking bankingRequest, Biller biller) throws ServiceException {
        if (bankingRequest.getServiceCode() != null) {
            if (bankingRequest.getServiceCode().equals(bankingProperties.getSoatBillerCode())) {
                validateSpecialFields(bankingRequest);
                soatValidation(bankingRequest, opType);
            }
            if (bankingRequest.getServiceCode().equals(bankingProperties.getPaynetBillerCode())) {
                validateSpecialFields(bankingRequest);
                paynetValidation(bankingRequest, opType, biller);
            }
            if (bankingRequest.getServiceCode().equals(bankingProperties.getBepsBillerCode())) {
                validateSpecialFields(bankingRequest);
                bepsValidation(bankingRequest, biller);
            }
        }
    }

    private void validateSpecialFields(RequestFormatBanking bankingRequest) throws ServiceException {
        if (!UtilHelper.stringNotNullOrNotEmpty(bankingRequest.getSpecialFields())) {
            StatusCode statusCode = statusCodeConfig.of("32");
            throw new CommunicationException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    private void soatValidation(RequestFormatBanking bankingRequest, OperationType opType) {
        if (opType.equals(OperationType.QUERY_BILL)) {
            bankingRequest.setReferenceNumber(bankingRequest.getSpecialFields());
            bankingRequest.setSpecialFields(null);
        } else {
            String[] echoData = bankingRequest.getEchoData().split(SPLIT_PIPE);
            String newDate = echoData[9].replace("-", "");
            String newReference = bankingRequest.getSpecialFields().concat("|").concat(newDate);
            bankingRequest.setReferenceNumber(newReference);
        }
    }

    private void paynetValidation(RequestFormatBanking bankingRequest, OperationType opType, Biller biller) {
        if (opType.equals(OperationType.QUERY_BILL)) {
            String[] fields = biller.getPlaceHolder().split(SPLIT_PIPE);
            String[] specialFields = bankingRequest.getSpecialFields().split(SPLIT_PIPE);
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].equals(bankingProperties.getPaynetDeletePlaceReference())) {
                    String newSpecial = bankingRequest.getSpecialFields().replace(specialFields[i], "");
                    newSpecial = newSpecial.endsWith("|") ? newSpecial.substring(0, newSpecial.length() - 1) : newSpecial;
                    bankingRequest.setSpecialFields(newSpecial);
                }
            }
            bankingRequest.setReferenceNumber(bankingRequest.getSpecialFields());
        } else {
            bankingRequest.setReferenceNumber(bankingRequest.getSpecialFields());
            bankingRequest.setEchoData(bankingRequest.getEchoData().split(SPLIT_PIPE)[0].concat("1"));
        }
    }

    private void bepsValidation(RequestFormatBanking bankingRequest, Biller biller) {
        String fieldToValidate;
        String[] fields = biller.getPlaceHolder().split(SPLIT_PIPE);
        String[] specialFields = bankingRequest.getSpecialFields().split(SPLIT_PIPE);
        if (bankingRequest.getSource().equals(Seller.CHANNEL.name())) {
            fieldToValidate = bankingProperties.getBepsDeletePlaceChannel();
        } else {
            fieldToValidate = bankingProperties.getBepsDeletePlaceSubscriber();
        }
        for (int i = 0; i < fields.length; i++) {
            if (specialFields[i].contains("/")) {
                bankingRequest.setSpecialFields(bankingRequest.getSpecialFields().replace("/", ""));
            }
            if (fields[i].equals(fieldToValidate)) {
                String newSpecial = bankingRequest.getSpecialFields().replace(specialFields[i], "");
                newSpecial = newSpecial.replace("||", "|");
                newSpecial = newSpecial.endsWith("|") ? newSpecial.substring(0, newSpecial.length() - 1) : newSpecial;
                bankingRequest.setSpecialFields(newSpecial);
            }
        }
        bankingRequest.setEchoData(bankingRequest.getSpecialFields());
    }

    //Valida la configuracion del convenio
    private void validateConfigBiller(RequestFormatBanking bankingRequest, OperationType opType, Biller biller) throws ProcessingException {
        StatusCode statusCode;
        if (biller.getWeftType() != null) {
            if (biller.getStatus().equals(ItemStatus.ENABLED) && biller.getWeftType().equals(WeftType.CONNECTOR)) {
                if (UtilHelper.stringNotNullOrNotEmpty(biller.getInternalCode())) {
                    bankingRequest.setInternalCode(biller.getInternalCode());
                    bankingRequest.setEan13BillerCode(biller.getEanCode());
                    bankingRequest.setPositionReference1(biller.getReferencePosition1());
                    bankingRequest.setLengthReference1(biller.getReferenceLength1());
                    bankingRequest.setServiceCode(biller.getBillerCode());
                    bankingRequest.setMaxValue(biller.getMaxValue());
                    bankingRequest.setMinValue(biller.getMinValue());
                    bankingRequest.setWeftType(WeftType.CONNECTOR);
                    if (opType.equals(OperationType.PAY_BILL)) {
                        billBusinessRules.executeRuleMultiple(bankingRequest, biller);
                        billBusinessRules.executeRuleMinMaxValue(bankingRequest, biller);
                    }
                } else {
                    statusCode = statusCodeConfig.of("30");
                    throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                }
            } else if (biller.getStatus().equals(ItemStatus.SUSPENDED)) {
                statusCode = statusCodeConfig.of("31");
                throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
            }
        }
    }

    private void executeRuleTypePaymentAllowed(RequestFormatBanking request) throws ProcessingException {
        if (bankingProperties.getGestorIdBogota().equals(request.getGestorId())) {
            validateTypePayBill(request.getTypePayBill(), bankingProperties.getPaymentBillAutomaticBogota(), bankingProperties.getPaymentBillManualBogota());
        } else if (bankingProperties.getGestorIdBBVA().equals(request.getGestorId())) {
            validateTypePayBill(request.getTypePayBill(), bankingProperties.getPaymentBillAutomaticBBVA(), bankingProperties.getPaymentBillManualBBVA());
        } else if (bankingProperties.getGestorIdAgrario().equals(request.getGestorId())) {
            validateTypePayBill(request.getTypePayBill(), bankingProperties.getPaymentBillAutomaticAgrario(), bankingProperties.getPaymentBillManualAgrario());
        }
    }

    private void validateTypePayBill(CollectionType type, boolean isAutomatic, boolean isManual) throws ProcessingException {
        StatusCode statusCode;
        switch (type) {
            case AUTOMATIC:
                if (!isAutomatic) {
                    statusCode = statusCodeConfig.of("34");
                    throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                }
                break;
            case MANUAL:
                if (!isManual) {
                    statusCode = statusCodeConfig.of("35");
                    throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                }
                break;
            default:
                statusCode = statusCodeConfig.of("1");
                throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
        }
    }

}

