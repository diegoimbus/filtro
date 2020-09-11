package co.moviired.microservice.provider.citibank;

import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.soap.Debtpayment;
import co.moviired.microservice.soap.DebtpaymentT;
import co.moviired.microservice.soap.Rdebtpayment;
import co.moviired.microservice.soap.RdebtpaymentT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@AllArgsConstructor
public class ReverseDeposit {

    private final BankProperties bankProperties;

    private static final String DATE_FORMAT = "yyMMdd";
    private static final String HOUR_FORMAT = "HHMMss";
    private static final String DATE_PAY_FORMAT = "yyyyMMdd";

    public Rdebtpayment parseRequestReverse(Object objPayment) {
        Rdebtpayment rdebtpayment = new Rdebtpayment();
        RdebtpaymentT rdebtpaymentT = new RdebtpaymentT();
        DebtpaymentT debtpaymentT = ((Debtpayment) objPayment).getDebtpayment();
        Date date = new Date();

        rdebtpaymentT.setMessageID(bankProperties.getDebtPayment().concat(getDateInformation(date, DATE_FORMAT)).concat(debtpaymentT.getPaymentReference()));
        rdebtpaymentT.setDateTime(getDateInformation(date, HOUR_FORMAT));
        rdebtpaymentT.setCountryCode(debtpaymentT.getCountryCode());
        rdebtpaymentT.setCollectorAccount(debtpaymentT.getCollectorAccount());
        rdebtpaymentT.setNetworkExtension(debtpaymentT.getNetworkExtension());
        rdebtpaymentT.setTransactionType("R");
        rdebtpaymentT.setUseAdditionalText(debtpaymentT.getUseAdditionalText());
        rdebtpaymentT.setPaymentReference(debtpaymentT.getPaymentReference());
        rdebtpaymentT.setOriginalPaymentReference(debtpaymentT.getPaymentReference());
        rdebtpaymentT.setPaymentCurrency(debtpaymentT.getPaymentCurrency());
        rdebtpaymentT.setDate(getDateInformation(date, DATE_PAY_FORMAT));
        rdebtpaymentT.setOriginalPaymentDate(debtpaymentT.getPaymentDate());
        rdebtpaymentT.setTotalPaymentAmount(debtpaymentT.getTotalPaymentAmount());
        rdebtpaymentT.setBranch(debtpaymentT.getBranch());

        rdebtpayment.setRdebtpayment(rdebtpaymentT);
        return rdebtpayment;
    }

    private String getDateInformation(Date currentDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(currentDate);
    }

}
