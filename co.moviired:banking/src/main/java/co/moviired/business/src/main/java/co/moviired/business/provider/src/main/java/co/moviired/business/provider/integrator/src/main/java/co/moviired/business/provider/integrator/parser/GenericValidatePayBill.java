package co.moviired.business.provider.integrator.parser;

import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import co.moviired.business.domain.jpa.movii.repository.IBillerRepository;
import co.moviired.business.helper.SignatureBuilder;
import co.moviired.business.helper.UtilHelper;
import co.moviired.business.properties.IntegratorProperties;
import co.moviired.business.provider.IRequest;
import co.moviired.business.provider.integrator.request.Data;
import co.moviired.business.provider.integrator.request.Meta;
import co.moviired.business.provider.integrator.request.RequestIntegrator;
import co.moviired.business.provider.integrator.request.RequestSignature;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@AllArgsConstructor
public class GenericValidatePayBill implements Serializable {

    private final IBillerRepository iBillerRepository;
    private final IntegratorProperties integratorProperties;

    public final IRequest generateRequest(RequestFormatBanking bankingRequest, Data data) throws JsonProcessingException {
        // META GENERICA
        Meta meta = new Meta();
        meta.setChannel(integratorProperties.getChanelEANCode());
        meta.setOriginAddress(integratorProperties.getOriginAdressEANCode());
        bankingRequest.setRequestDate(new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date()));
        meta.setRequestDate(bankingRequest.getRequestDate());
        meta.setRequestReference(bankingRequest.getRequestDate().replace(".", ""));
        meta.setSystemId(integratorProperties.getSystemIdEANCode());
        meta.setUserName(integratorProperties.getUserNameEANCode());
        meta.setPasswordHash(integratorProperties.getPasswordEANCode());
        meta.setCustomerId(integratorProperties.getCustomerIdEANCode());
        meta.setDeviceCode(integratorProperties.getDeviceCodeEANCode());

        RequestSignature signature = new RequestSignature();
        String systemSignature = new SignatureBuilder()
                .append(meta)
                .append(data)
                .append(integratorProperties.getSaltEANCode())
                .build();
        signature.setSystemSignature(systemSignature);

        RequestIntegrator requestIntegrator = new RequestIntegrator();
        requestIntegrator.setMeta(meta);
        requestIntegrator.setData(data);
        requestIntegrator.setRequestSignature(signature);
        return requestIntegrator;
    }

    public final Biller validationAndFindBiller(co.moviired.business.provider.integrator.response.Data dataIntegrator, RequestFormatBanking banking) {
        Biller biller = new Biller();
        if (UtilHelper.stringNotNullOrNotEmpty(dataIntegrator.getBillerName()))
            biller.setName(dataIntegrator.getBillerName());

        if (UtilHelper.stringNotNullOrNotEmpty(dataIntegrator.getPartialPayment()))
            biller.setPartialPayment(UtilHelper.validateBooleanInString(dataIntegrator.getPartialPayment()));

        if (biller.getName() == null || biller.getPartialPayment() == null) {
            Biller billerDataBase;
            if (banking != null)
                billerDataBase = iBillerRepository.getByBillerCode(banking.getServiceCode());
            else
                billerDataBase = iBillerRepository.getByEanCode(dataIntegrator.getEan13Billercode());

            if (billerDataBase != null) {
                biller.setName(billerDataBase.getName() == null ? "" : billerDataBase.getName());
                biller.setPartialPayment(billerDataBase.getPartialPayment() != null && billerDataBase.getPartialPayment());
            } else {
                biller.setName("");
                biller.setPartialPayment(false);
            }
        }
        return biller;
    }

}

