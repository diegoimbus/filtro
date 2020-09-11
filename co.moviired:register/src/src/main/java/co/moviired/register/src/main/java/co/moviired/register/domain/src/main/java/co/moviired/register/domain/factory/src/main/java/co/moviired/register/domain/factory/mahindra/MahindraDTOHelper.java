package co.moviired.register.domain.factory.mahindra;

import co.moviired.register.domain.dto.MahindraDTO;
import co.moviired.register.properties.MahindraProperties;

import java.math.BigDecimal;

public final class MahindraDTOHelper {

    private MahindraDTOHelper() {
        super();
    }

    public static MahindraDTO getLoginRequest(MahindraProperties mahindraProperties, String phoneNumber, String password) {
        return MahindraDTO.builder()
                .isPinCheckReq(mahindraProperties.getLoginIsPinCheckReq())
                .language1(mahindraProperties.getLoginLanguage1())
                .mPin(password)
                .msisdn(phoneNumber)
                .otpReq(mahindraProperties.getLoginOtpReq())
                .provider(mahindraProperties.getLoginProvider())
                .source(mahindraProperties.getLoginSource())
                .type(mahindraProperties.getLoginType())
                .build();
    }

    public static MahindraDTO getCashInRequest(MahindraProperties mahindraProperties, String ftxId, String phoneNumber, String idNumber, BigDecimal value, String cellId, String remarks) {
        return MahindraDTO.builder()
                .type(mahindraProperties.getRciReqType())
                .ftxId(ftxId)
                .msisdn(mahindraProperties.getRciReqMsisdn())
                .pin(mahindraProperties.getRciReqPin())
                .mPin(mahindraProperties.getRciReqPin())
                .msisdn2(phoneNumber)
                .cellId(cellId)
                .remarks(remarks)
                .idNo(idNumber)
                .amount(value.toString())
                .sndProvider(mahindraProperties.getRciReqSndProvider())
                .rcvProvider(mahindraProperties.getRciReqRcvProvider())
                .sndInstrument(mahindraProperties.getRciReqSndInstrument())
                .rcvInstrument(mahindraProperties.getRciReqRcvInstrument())
                .language1(mahindraProperties.getRciReqLanguage1())
                .language2(mahindraProperties.getRciReqLanguage2())
                .build();
    }
}

