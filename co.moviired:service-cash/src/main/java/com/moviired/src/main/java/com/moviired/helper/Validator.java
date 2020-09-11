package com.moviired.helper;

import co.moviired.base.domain.exception.DataException;
import com.moviired.model.Network;
import com.moviired.model.request.CashOutRequest;

import java.io.Serializable;
import java.text.ParseException;

public final class Validator implements Serializable {

    private static final long serialVersionUID = 292241962623576645L;

    private Validator() {
    }

    public static void validateFields(CashOutRequest request, Network.NetworkDetail networkDetail, ProcessType processType) throws DataException {

        if ((request.getIssuerId() == null) || (request.getIssuerId().isEmpty())) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo issuerId es obligatorio");
        }

        if ((request.getIssuerName() == null) || (request.getIssuerName().isEmpty())) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo issuerName es obligatorio");
        }

        if (request.getIssueDate() == null) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo issueDate es obligatorio");
        } else {
            try {
                Utilidad.dateFormat(request.getIssueDate());
            } catch (ParseException e) {
                throw new DataException(Constant.DATA_FIELDS_EXECPTION, "formato incorrecto del campo issueDate");
            }
        }

        if ((request.getSource() == null) || (request.getSource().isEmpty())) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo source es obligatorio");
        }

        if ((ProcessType.PENDING_BY_MERCHANT.equals(processType)) && ((request.getCorrelationId() == null) || (request.getCorrelationId().isEmpty()))) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo correlationId es obligatorio");
        }

        if ((ProcessType.INITIALIZATE_CASHOUT.equals(processType)) && ((request.getAmount() == null) || (request.getAmount() <= 0))) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo amount es obligatorio o tiene que ser mayor a 1000");
        }

        if (ProcessType.COMPLETE_CASHOUT.equals(processType)) {
            //validacion campos cashOut en 1 paso
            if (networkDetail.getProtocol() == 2) {
                if ((request.getAmount() == null) || (request.getAmount() <= 0)) {
                    throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo amount es obligatorio o tiene que ser mayor a 0");
                }

                if ((request.getOtp() == null) || (request.getOtp().isEmpty())) {
                    throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo otp es obligatorio");
                }
            }

            if (networkDetail.getProtocol() == 1) {
                if (request.getCashOutId() == null) {
                    throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo cashOutId es obligatorio");
                }

                if ((request.getToken() == null) || (request.getToken().isEmpty())) {
                    throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo token es obligatorio");
                }
            }
        }

        if (((ProcessType.DECLINE_CASHOUT.equals(processType)) || (ProcessType.REVERSE_CASHOUT.equals(processType))) && (networkDetail.getProtocol() != Constant.PROTOCOL_THREE) && (request.getCashOutId() == null)) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo cashOutId es obligatorio");
        }

        if ((ProcessType.PENDING_BY_MERCHANT.equals(processType)) && ((request.getOtp() == null) || (request.getOtp().isEmpty()))) {
            throw new DataException(Constant.DATA_FIELDS_EXECPTION, "El campo otp es obligatorio");
        }

        request.setCorrelationId(Utilidad.assignCorrelative(request.getCorrelationId()));
    }
}

