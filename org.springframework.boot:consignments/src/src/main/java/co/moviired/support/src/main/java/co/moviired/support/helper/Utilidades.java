package co.moviired.support.helper;

import co.moviired.support.domain.dto.ConsignmentDetailDTO;
import co.moviired.support.domain.enums.ConsignmentStatus;
import co.moviired.support.domain.request.impl.ConsignmentSearch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Utilidades {

    private Utilidades() {
        super();
    }

    public static boolean isInteger(String numero) {
        try {
            Long.parseLong(numero);
            return true;
        } catch (NumberFormatException e) {
            log.error("Ocurrio un error al intentar formatear el numero: " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean getFilter(Object consig, Object filter) {
        return null == filter || String.valueOf(consig).contains(String.valueOf(filter));
    }

    public static ConsignmentDetailDTO getConsignmentDetailDTO(ConsignmentSearch requestConsignmentSearch) {
        return ConsignmentDetailDTO.builder().agreementNumber(requestConsignmentSearch.getAgreementNumber())
                .amount(requestConsignmentSearch.getAmount()).approvementId(requestConsignmentSearch.getApprovementId())
                .bankId(requestConsignmentSearch.getBankId()).bankName(requestConsignmentSearch.getBankName())
                .branchOffice(requestConsignmentSearch.getBranchOffice())
                .city(requestConsignmentSearch.getCity()).correlationId(requestConsignmentSearch.getCorrelationId())
                .id(requestConsignmentSearch.getId()).msisdn(requestConsignmentSearch.getMsisdn())
                .paymentDate(requestConsignmentSearch.getPaymentDate())
                .paymentReference(requestConsignmentSearch.getPaymentReference())
                .processDate(requestConsignmentSearch.getProcessDate()).reason(requestConsignmentSearch.getReason())
                .state(requestConsignmentSearch.getState()).status(ConsignmentStatus.parse(requestConsignmentSearch.getStatus()))
                .txnid(requestConsignmentSearch.getTxnid()).build();
    }

}

