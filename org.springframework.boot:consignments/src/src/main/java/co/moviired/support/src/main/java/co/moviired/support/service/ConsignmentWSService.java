package co.moviired.support.service;

import co.moviired.support.domain.entity.mysql.ConsignmentWS;
import co.moviired.support.domain.enums.ConsignmentWSStatus;
import co.moviired.support.domain.repository.mysql.IConsignmentWSRepository;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.NotifyBillPaymentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.RevertBillPaymentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.NotifyBillPaymentOutDTO;
import co.moviired.support.endpoint.bancolombia.dto.DataConsignmentRequest;
import co.moviired.support.endpoint.bancolombia.dto.DataConsignmentResponse;
import co.moviired.support.endpoint.bancolombia.dto.DataRevertRequest;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class ConsignmentWSService implements Serializable {

    private static final long serialVersionUID = -1143184049994629351L;

    private final IConsignmentWSRepository consignmentWSRepository;
    private final Utilities utilities;

    private static final String LOG_COMPONENT = "PROCESS ConsignmentWSService";

    public ConsignmentWSService(IConsignmentWSRepository consignmentWSRepository,
                                Utilities utilities) {
        this.consignmentWSRepository = consignmentWSRepository;
        this.utilities = utilities;
    }

    public NotifyBillPaymentOutDTO consignmentRegistryBogota(NotifyBillPaymentInDTO notifyBillPaymentInDTO){

        NotifyBillPaymentOutDTO responseDTO = new NotifyBillPaymentOutDTO();
        Date date = new Date();
        Timestamp now = new Timestamp(date.getTime());
        String status = ConsignmentWSStatus.PENDING.getId();

        log.info(LOG_COMPONENT + " - consignmentRegistry");

        ConsignmentWS consignmentWSBog = new ConsignmentWS();

        consignmentWSBog.setAccountNumber(notifyBillPaymentInDTO.getAccountNumber());
        consignmentWSBog.setAccountType(notifyBillPaymentInDTO.getAccountType());
        consignmentWSBog.setAccountantDate(notifyBillPaymentInDTO.getAccountantDate());
        consignmentWSBog.setAgreementCode(notifyBillPaymentInDTO.getAgreementCode());
        consignmentWSBog.setBankNitOne(notifyBillPaymentInDTO.getBankNitOne());
        consignmentWSBog.setBankNitTwo(notifyBillPaymentInDTO.getBankNitTwo());
        consignmentWSBog.setBillNumber(notifyBillPaymentInDTO.getBillNumber());
        consignmentWSBog.setBillTotalAmount(notifyBillPaymentInDTO.getBillTotalAmount());
        consignmentWSBog.setBillingCompanyAgreementOne(notifyBillPaymentInDTO.getBillingCompanyAgreementOne());
        consignmentWSBog.setBillingCompanyAgreementTwo(notifyBillPaymentInDTO.getBillingCompanyAgreementTwo());
        consignmentWSBog.setBillingCompanyNameOne(notifyBillPaymentInDTO.getBillingCompanyNameOne());
        consignmentWSBog.setBillingCompanyNameTwo(notifyBillPaymentInDTO.getBillingCompanyNameTwo());
        consignmentWSBog.setCheckAmount(notifyBillPaymentInDTO.getCheckAmount());
        consignmentWSBog.setCompensationCodeOne(notifyBillPaymentInDTO.getCompensationCodeOne());
        consignmentWSBog.setCompensationCodeTwo(notifyBillPaymentInDTO.getCompensationCodeTwo());
        consignmentWSBog.setCurrencyType(notifyBillPaymentInDTO.getCurrencyType());
        consignmentWSBog.setEanCode(notifyBillPaymentInDTO.getEanCode());
        consignmentWSBog.setEffectiveAmount(notifyBillPaymentInDTO.getEffectiveAmount());
        consignmentWSBog.setOfficeCode(notifyBillPaymentInDTO.getOfficeCode());
        consignmentWSBog.setReferenceFieldTwo(notifyBillPaymentInDTO.getReferenceFieldTwo());
        consignmentWSBog.setReferenceFieldThree(notifyBillPaymentInDTO.getReferenceFieldThree());
        consignmentWSBog.setReferenceFieldFour(notifyBillPaymentInDTO.getReferenceFieldFour());
        consignmentWSBog.setReferenceFieldFive(notifyBillPaymentInDTO.getReferenceFieldFive());
        consignmentWSBog.setTransactionChannel(notifyBillPaymentInDTO.getTransactionChannel());
        consignmentWSBog.setTransactionDate(notifyBillPaymentInDTO.getTransactionDate());
        consignmentWSBog.setUuid(notifyBillPaymentInDTO.getUuid());
        consignmentWSBog.setWorkingDay(notifyBillPaymentInDTO.getWorkingDay());
        consignmentWSBog.setCreateDate(now);
        consignmentWSBog.setStatusCode(status);
        consignmentWSBog.setCorrelationId(utilities.asignarCorrelativo(null));

        consignmentWSRepository.save(consignmentWSBog);

        responseDTO.setAuthorizationNumber(consignmentWSBog.getId().toString());
        responseDTO.setCode(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode());
        responseDTO.setCorrelationId(consignmentWSBog.getCorrelationId());
        log.info(LOG_COMPONENT + "  - consignmentRegistryBogota - Se registro la consignacion correctamente");
        return responseDTO;
    }

    public DataConsignmentResponse consignmentRegistryBancolombia(DataConsignmentRequest request){

        DataConsignmentResponse responseDTO = new DataConsignmentResponse();

        Date date = new Date();
        Timestamp now = new Timestamp(date.getTime());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        DateFormat hourFormat = new SimpleDateFormat("HHmmss");
        String accountantDate = dateFormat.format(date);
        String accountantHour = hourFormat.format(date);
        String status = ConsignmentWSStatus.PENDING.getId();

        log.info(LOG_COMPONENT + " - consignmentRegistry");

        ConsignmentWS consignmentWSBC = new ConsignmentWS();

        consignmentWSBC.setAccountNumber(request.getPhoneNumber());
        consignmentWSBC.setAccountType("1");
        consignmentWSBC.setAccountantDate(accountantDate);
        consignmentWSBC.setBankNitOne(request.getBankId());
        consignmentWSBC.setBillNumber(request.getPhoneNumber());
        consignmentWSBC.setBillTotalAmount(request.getAmount());
        consignmentWSBC.setBillingCompanyAgreementOne(request.getReferenceId());
        consignmentWSBC.setUuid(request.getBlockSms());
        consignmentWSBC.setReferenceFieldTwo(request.getTxnMode());
        consignmentWSBC.setReferenceFieldThree(request.getCellId());
        consignmentWSBC.setReferenceFieldFour(request.getFtxnId());
        consignmentWSBC.setReferenceFieldFive(request.getRemarks());
        consignmentWSBC.setCreateDate(now);
        consignmentWSBC.setStatusCode(status);
        consignmentWSBC.setCorrelationId(utilities.asignarCorrelativo(null));

        consignmentWSRepository.save(consignmentWSBC);

        responseDTO.setTxnId(consignmentWSBC.getId().toString());
        responseDTO.setResponseCode(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode());
        responseDTO.setCorrelationId(consignmentWSBC.getCorrelationId());
        responseDTO.setDate(accountantDate);
        responseDTO.setTime(accountantHour);
        log.info(LOG_COMPONENT + "  - consignmentRegistryBancolombia - Se registro la consignacion correctamente");
        return responseDTO;

    }

    public boolean consignmentStatusMahindraBogota(@NotNull NotifyBillPaymentInDTO notifyBillPaymentInDTO){

        boolean approved = true;

        String accountNumberBog = notifyBillPaymentInDTO.getAccountNumber();
        String accountantDateBog = notifyBillPaymentInDTO.getAccountantDate();
        String bankNitOneBog = notifyBillPaymentInDTO.getBankNitOne();
        String transactionChannelBog = notifyBillPaymentInDTO.getTransactionChannel();
        String eanCodeBog = notifyBillPaymentInDTO.getEanCode();
        String billTotalAmountBog = notifyBillPaymentInDTO.getBillTotalAmount();
        String billNumberBog = notifyBillPaymentInDTO.getBillNumber();
        String uuidBog = notifyBillPaymentInDTO.getUuid();
        String statusCodeBog = ConsignmentWSStatus.APPROVED.getId();

        //Se concatenan varibales para cumplir con la revision de SonarLint
        String concatenatedBog = accountNumberBog +
                accountantDateBog +
                bankNitOneBog +
                transactionChannelBog +
                eanCodeBog +
                billTotalAmountBog +
                billNumberBog +
                uuidBog +
                statusCodeBog;

        if(!consignmentWSRepository.findByUnique(concatenatedBog).isEmpty()){
            approved = false;
            log.info(LOG_COMPONENT + " - consignmentStatusMahindra - Consignacion registrada");
        }else{
            log.info(LOG_COMPONENT + " - consignmentStatusMahindra - Consignacion no registrada");
        }

        return approved;
    }

    public boolean consignmentStatusMahindraBancolombia(@NotNull DataConsignmentRequest request){

        boolean approved = true;
        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("yyyyMMdd");

        String billNumberBC = request.getPhoneNumber();
        String accountantDateBC = hourFormat.format(date);
        String bankNitOneBC = request.getBankId();
        String uuidBC = request.getBlockSms();
        String billTotalAmountBC = request.getAmount();
        String billingCompanyAgreementOneBC = request.getReferenceId();
        String referenceFieldTwoBC = request.getTxnMode();
        String referenceFieldThreeBC = request.getCellId();
        String referenceFieldFourBC = request.getFtxnId();
        String referenceFieldFiveBC = request.getRemarks();
        String statusCodeBC = ConsignmentWSStatus.APPROVED.getId();

        //Se concatenan varibales para cumplir con la revision de SonarLint
        String concatenatedBC = billNumberBC +
                accountantDateBC +
                bankNitOneBC +
                uuidBC +
                billTotalAmountBC +
                billingCompanyAgreementOneBC +
                referenceFieldTwoBC +
                referenceFieldThreeBC +
                referenceFieldFourBC +
                referenceFieldFiveBC +
                statusCodeBC;

        if(!consignmentWSRepository.findByUniqueBancolombia(concatenatedBC).isEmpty()){
            approved = false;
            log.info(LOG_COMPONENT + " - consignmentStatusMahindraBancolombia - Consignacion registrada");
        }else{
            log.info(LOG_COMPONENT + " - consignmentStatusMahindraBancolombia - Consignacion no registrada");
        }

        return approved;
    }

    public void consignmentUpdate(Integer id, @NotNull String statusCode, String transactionIdentificator){

        ConsignmentWS consignmentWS = consignmentWSRepository.findId(id);

        if(statusCode.equals(ConsignmentWSStatus.APPROVED.getId())){
            consignmentWS.setMahindraTransactionId(transactionIdentificator);
        }else if(statusCode.equals(ConsignmentWSStatus.REVERSED.getId())){
            consignmentWS.setRevertTransactionId(transactionIdentificator);
        }

        consignmentWS.setStatusCode(statusCode);

        consignmentWSRepository.save(consignmentWS);
        log.info(LOG_COMPONENT + " - consignmentUpdate - El estado de la consignacion se actualizo correctamente");

    }

    public ConsignmentWS consignmentRevertMahindra(@NotNull RevertBillPaymentInDTO revertBillPaymentInDTO){

        ConsignmentWS consignmentWS;

        String accountNumberRevert = revertBillPaymentInDTO.getAccountNumberRevert();
        String accountantDateRevert = revertBillPaymentInDTO.getAccountantDateRevert();
        String bankNitOneRevert = revertBillPaymentInDTO.getBankNitRevert();
        String transactionChannelRevert = revertBillPaymentInDTO.getTransaccionChannel();
        String billTotalAmountRevert = revertBillPaymentInDTO.getTotalBillAmountRevert();
        String billNumberRevert = revertBillPaymentInDTO.getBillNumberRevert();
        String uuidRevert = revertBillPaymentInDTO.getUuidRevert();
        String statusCodeRevert = ConsignmentWSStatus.APPROVED.getId();

        //Se concatenan varibales para cumplir con la revision de SonarLint
        String concatenatedRevert = accountNumberRevert +
                accountantDateRevert +
                bankNitOneRevert +
                transactionChannelRevert +
                billTotalAmountRevert +
                billNumberRevert +
                uuidRevert +
                statusCodeRevert;

        consignmentWS = consignmentWSRepository.findByRevert(concatenatedRevert);

        if(consignmentWS == null){
            log.info(LOG_COMPONENT + " - consignmentRevertMahindra - Consignacion no registrada");
        }else{
            consignmentWS.setCorrelationIdRevert(utilities.asignarCorrelativo(null));
            consignmentWSRepository.save(consignmentWS);
            log.info(LOG_COMPONENT + " - consignmentRevertMahindra - generacion de correlationId para la reversion - Consignacion registrada");
        }

        return consignmentWS;
    }

    public ConsignmentWS consignmentRevertMahindraBancolombia(@NotNull DataRevertRequest request){

        ConsignmentWS consignmentWS;

        String billNumberRevertBC = request.getPhoneNumber();
        String accountantDateRevertBC = request.getAccountantDate();
        String bankNitOneRevertBC = request.getBankId();
        String uuidRevertBC = request.getBlockSms();
        String billTotalAmountRevertBC = request.getAmount();
        String billingCompanyAgreementOneRevertBC = request.getReferenceId();
        String referenceFieldTwoRevertBC = request.getTxnMode();
        String referenceFieldThreeRevertBC = request.getCellId();
        String referenceFieldFourRevertBC = request.getFtxnId();
        String referenceFieldFiveRevertBC = request.getRemarks();
        String statusCodeRevertBC = ConsignmentWSStatus.APPROVED.getId();

        //Se concatenan varibales para cumplir con la revision de SonarLint
        String concatenatedRevertBC = billNumberRevertBC +
                accountantDateRevertBC +
                bankNitOneRevertBC +
                uuidRevertBC +
                billTotalAmountRevertBC +
                billingCompanyAgreementOneRevertBC +
                referenceFieldTwoRevertBC +
                referenceFieldThreeRevertBC +
                referenceFieldFourRevertBC +
                referenceFieldFiveRevertBC +
                statusCodeRevertBC;

        consignmentWS = consignmentWSRepository.findByRevertBancolombia(concatenatedRevertBC);

        if(consignmentWS == null){
            log.info(LOG_COMPONENT + " - consignmentRevertMahindraBancolombia - Consignacion no registrada");
        }else{
            consignmentWS.setCorrelationIdRevert(utilities.asignarCorrelativo(null));
            consignmentWSRepository.save(consignmentWS);
            log.info(LOG_COMPONENT + " - consignmentRevertMahindraBancolombia - generacion de correlationId para la reversion - Consignacion registrada");
        }

        return consignmentWS;
    }

    public boolean consignmentVerRevertMahindra(@NotNull RevertBillPaymentInDTO revertBillPaymentInDTO){

        boolean revert;
        ConsignmentWS consignmentWS;

        String accountNumberVerRevert = revertBillPaymentInDTO.getAccountNumberRevert();
        String accountantDateVerRevert = revertBillPaymentInDTO.getAccountantDateRevert();
        String bankNitOneVerRevert = revertBillPaymentInDTO.getBankNitRevert();
        String transactionChannelVerRevert = revertBillPaymentInDTO.getTransaccionChannel();
        String billTotalAmountVerRevert = revertBillPaymentInDTO.getTotalBillAmountRevert();
        String billNumberVerRevert = revertBillPaymentInDTO.getBillNumberRevert();
        String uuidVerRevert = revertBillPaymentInDTO.getUuidRevert();
        String statusCodeVerRevert = ConsignmentWSStatus.REVERSED.getId();

        //Se concatenan varibales para cumplir con la revision de SonarLint
        String concatenatedVerRevert = accountNumberVerRevert +
                accountantDateVerRevert +
                bankNitOneVerRevert +
                transactionChannelVerRevert +
                billTotalAmountVerRevert +
                billNumberVerRevert +
                uuidVerRevert +
                statusCodeVerRevert;

        consignmentWS = consignmentWSRepository.findByRevert(concatenatedVerRevert);

        if(consignmentWS == null){
            log.info(LOG_COMPONENT + " - consignmentRevertMahindra - Consignacion no rechazada");
            revert = false;
        }else{
            log.info(LOG_COMPONENT + " - consignmentRevertMahindra - Consignacion rechazada");
            revert = true;
        }

        return revert;
    }

    public boolean consignmentVerRevertMahindraBancolombia(@NotNull DataRevertRequest request){

        boolean revert;
        ConsignmentWS consignmentWS;

        String billNumberVerRevertBC = request.getPhoneNumber();
        String accountantDateVerRevertBC = request.getAccountantDate();
        String bankNitOneVerRevertBC = request.getBankId();
        String uuidVerRevertBC = request.getBlockSms();
        String billTotalAmountVerRevertBC = request.getAmount();
        String billingCompanyAgreementOneVerRevertBC = request.getReferenceId();
        String referenceFieldTwoVerRevertBC = request.getTxnMode();
        String referenceFieldThreeVerRevertBC = request.getCellId();
        String referenceFieldFourVerRevertBC = request.getFtxnId();
        String referenceFieldFiveVerRevertBC = request.getRemarks();
        String statusCodeVerRevertBC = ConsignmentWSStatus.REVERSED.getId();

        //Se concatenan varibales para cumplir con la revision de SonarLint
        String concatenatedVerRevertBC = billNumberVerRevertBC +
                accountantDateVerRevertBC +
                bankNitOneVerRevertBC +
                uuidVerRevertBC +
                billTotalAmountVerRevertBC +
                billingCompanyAgreementOneVerRevertBC +
                referenceFieldTwoVerRevertBC +
                referenceFieldThreeVerRevertBC +
                referenceFieldFourVerRevertBC +
                referenceFieldFiveVerRevertBC +
                statusCodeVerRevertBC;

        consignmentWS = consignmentWSRepository.findByRevertBancolombia(concatenatedVerRevertBC);

        if(consignmentWS == null){
            log.info(LOG_COMPONENT + " - consignmentRevertMahindra - Consignacion no rechazada");
            revert = false;
        }else{
            log.info(LOG_COMPONENT + " - consignmentRevertMahindra - Consignacion rechazada");
            revert = true;
        }

        return revert;
    }

}

