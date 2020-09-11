package co.moviired.transaction.model.dto;

import co.moviired.transaction.helper.ConstantsHelper;
import co.moviired.transaction.helper.HistoryConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TransactionDTO implements Serializable {

    private static final long serialVersionUID = 4668053354443228990L;
    private static final String CREDITO_MOVIIRED = "CRÉDITO MOVIIRED";
    private static final String GIRO_MOVIIRED = "GIRO FINANCIERO";
    private static final String SEPARATOR = "\\|";
    private String serviceType = "-";
    private String serviceSubType = "-";
    private String name = "-";
    private String nameDetail = "-";
    private String referenceNumber = "-";
    private String transferId = "-";
    private String source = "-";
    private String transactionDate = "-";
    private String transferValue = "-";
    private String commission = "0";
    private String transferStatus = "-";
    private String gestorId = "-";
    private String manager = "-";
    private String from = "-";
    private String productCode = "-";
    private String authorizationNumber = "-";
    private String remarks = "-";
    private String txntType = "-";

    public TransactionDTO(@NotNull ResultSet rs) throws SQLException {
        super();

        String pServiceType = rs.getString(HistoryConstants.FLD_SERVICE_TYPE);
        String pServiceSubType = rs.getString(HistoryConstants.FLD_TRANSFER_SUBTYPE);
        String pTransferId = rs.getString(HistoryConstants.FLD_TRANSFER_ID);
        Date pTransferOn = rs.getTimestamp(HistoryConstants.FLD_TRANSFER_ON);
        String pTransferStatus = rs.getString(HistoryConstants.FLD_TRANSFER_STATUS);
        String pExtTxnNumber = rs.getString(HistoryConstants.FLD_EXT_TXN_NUMBER);
        String pReferenceNumber = rs.getString(HistoryConstants.FLD_REFERENCE_NUMBER);
        String attr1Value = rs.getString(HistoryConstants.FLD_ATTR_1_VALUE);
        String attr2Value = rs.getString(HistoryConstants.FLD_ATTR_2_VALUE);
        String attr3Value = rs.getString(HistoryConstants.FLD_ATTR_3_VALUE);
        String attr4Value = rs.getString(HistoryConstants.FLD_ATTR_4_VALUE);
        String attr5Value = rs.getString(HistoryConstants.FLD_ATTR_5_VALUE);
        String attr6Value = rs.getString(HistoryConstants.FLD_ATTR_6_VALUE);
        double pTransferValue = rs.getDouble(HistoryConstants.FLD_TRANSFER_VALUE);
        double pRequestValue = rs.getDouble(HistoryConstants.FLD_REQUEST_VALUE);
        String pRemarks = rs.getString(HistoryConstants.FLD_REMARKS);
        String pTxntType = rs.getString(HistoryConstants.FLD_TXN_TYPE);

        // Campos opcionales
        Double approvedValue = null;
        String accountId = null;
        String secondPartyAccountIdCashIn = null;
        String secondPartyAccountIdCashOut = null;
        try {
            approvedValue = rs.getDouble(HistoryConstants.FLD_APPROVED_VALUE);
            accountId = rs.getString(HistoryConstants.FLD_ACCOUNT_ID);
            secondPartyAccountIdCashIn = rs.getString(HistoryConstants.FLD_SECOND_PARTY_ACCOUNT_ID);
            secondPartyAccountIdCashOut = rs.getString(HistoryConstants.FLD_SECOND_PARTY_ACCOUNT_ID2);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (null != pServiceType) {
            this.serviceType = pServiceType;
        }
        if (null != pServiceSubType) {
            this.serviceSubType = pServiceSubType;
        }
        if (null != pTransferId) {
            this.transferId = pTransferId;
        }
        if (null != pTransferOn) {
            this.transactionDate = pTransferOn.toString();
        }

        this.transferValue = String.valueOf(pTransferValue / ConstantsHelper.NUMBER_10000);

        if (null != pTransferStatus) {
            this.transferStatus = pTransferStatus;
        }
        if (null != accountId) {
            this.from = accountId;
        }
        if (null != attr4Value) {
            this.productCode = attr4Value;
        }
        if (null != pRemarks) {
            this.remarks = pRemarks;
        }
        if (null != pTxntType) {
            this.txntType = pTxntType;
        }
        if (null != approvedValue && this.transferStatus.equals("TS")) {
            this.commission = String.valueOf(approvedValue / ConstantsHelper.NUMBER_10000);
        }

        if (!"CONTPRCHS".equals(pServiceSubType) && null != attr5Value && attr5Value.contains("|")) {
            this.source = attr5Value.split(SEPARATOR)[2];
        }

        if (pServiceSubType == null) {
            pServiceSubType = "DEFAULT";
        }

        switch (pServiceSubType) {
            case "RC":
                if (null != attr1Value) {
                    this.referenceNumber = attr1Value;
                }
                if (null != attr2Value) {
                    this.authorizationNumber = attr2Value.split("_")[0];
                }
                if (null != attr6Value) {
                    this.name = attr6Value.toUpperCase();
                }
                if (null != attr2Value) {
                    this.gestorId = attr2Value.split(SEPARATOR)[0];
                }

                break;

            case "CONTPRCHS":
                if (null != attr1Value) {
                    this.referenceNumber = attr1Value;
                }
                if (null != attr3Value) {
                    this.authorizationNumber = attr3Value;
                }

                break;

            case "SRBILPAY":

                //YABX
                if (this.serviceType.equals("SRBILPAY")) {
                    if (null != attr3Value) {
                        this.referenceNumber = attr3Value;
                    }

                    this.name = this.remarks;
                    this.nameDetail = this.remarks;
                    this.authorizationNumber = pTransferId;
                }

                break;

            case "ONLINEBP":
                if (null != attr3Value && attr3Value.contains("_")) {
                    this.referenceNumber = attr3Value.split("_")[1];
                    if (this.referenceNumber.contains("|")) {
                        String[] split = this.referenceNumber.split(SEPARATOR);
                        this.referenceNumber = split.length == 2 ? split[1] : split[0];
                    }
                }
                if (null != attr2Value) {
                    this.authorizationNumber = attr2Value.contains("|") ? attr2Value.split(SEPARATOR)[1] : attr2Value;
                }

                if (null != attr6Value) {
                    this.name = attr6Value.toUpperCase();
                }
                if (null != attr3Value) {
                    this.gestorId = attr3Value.split("_")[0];
                }

                break;

            case "CASHIN":
            case "C2C":
                if (null != secondPartyAccountIdCashIn) {
                    this.referenceNumber = secondPartyAccountIdCashIn;
                }

                this.authorizationNumber = pTransferId;

                if (this.serviceType.equals("LOANREPAY")) {
                    this.name = CREDITO_MOVIIRED;
                    this.nameDetail = "PAGASTE " + CREDITO_MOVIIRED;
                } else if (this.serviceType.equals("LOANDISB")) {
                    this.name = CREDITO_MOVIIRED;
                    this.nameDetail = "RECIBISTE " + CREDITO_MOVIIRED;
                } else {
                    this.name = pServiceSubType;
                    this.nameDetail = (this.txntType.equals("CR")) ? "RECIBISTE" : "ENVIASTE";
                    this.gestorId = pServiceSubType;
                }

                break;

            case "CASHOUT":
                if (null != secondPartyAccountIdCashOut) {
                    this.referenceNumber = secondPartyAccountIdCashOut;
                }

                this.authorizationNumber = pTransferId;
                this.name = pServiceSubType;
                this.gestorId = pServiceSubType;
                break;

            case "O2C":
                if (this.remarks.contains("|")) {
                    String[] split = this.remarks.split(SEPARATOR);
                    try {
                        if (ConstantsHelper.NUMBER_6 == split.length) {
                            this.referenceNumber = split[ConstantsHelper.NUMBER_5];
                            this.name = split[ConstantsHelper.NUMBER_4];
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }

                if (null != pReferenceNumber) {
                    this.authorizationNumber = pReferenceNumber;
                }

                this.gestorId = pServiceSubType;

                if (this.serviceType.equals("AUTOO2C")) {
                    this.transferValue = String.valueOf(pRequestValue / ConstantsHelper.NUMBER_10000);
                }

                break;

            case "CINBYAGNT":
                this.authorizationNumber = pTransferId;
                this.name = GIRO_MOVIIRED;
                this.nameDetail = "COLOCASTE " + GIRO_MOVIIRED;
                this.referenceNumber = this.transferId;
                this.authorizationNumber = pReferenceNumber;

                break;

            case "COBYPCODE":
                this.authorizationNumber = pTransferId;
                this.name = GIRO_MOVIIRED;
                this.nameDetail = "PAGASTE " + GIRO_MOVIIRED;
                this.referenceNumber = this.transferId;
                this.authorizationNumber = pReferenceNumber;

                break;

            default:
                if (null != pExtTxnNumber) {
                    this.referenceNumber = pExtTxnNumber;
                }
                if (null != attr6Value) {
                    this.name = attr6Value.toUpperCase();
                }
                this.authorizationNumber = pTransferId;
        }
    }

}

