package co.moviired.support.endpoint.bancobogota.dto.consignment.in;

import java.io.Serializable;

public class ConsignmentInDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idConsignment;
    private String client;
    private String amount;
    private String bank;
    private String movilRedKey;
    private String externalReference;
    private String documentDate;
    private Integer regId;
    private String correlationId;

    public ConsignmentInDTO() {
        // Do nothing
    }

    public Integer getRegId() {
        return this.regId;
    }

    public void setRegId(Integer regId) {
        this.regId = regId;
    }

    public String getClient() {
        return this.client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMovilRedKey() {
        return this.movilRedKey;
    }

    public void setMovilRedKey(String movilRedKey) {
        this.movilRedKey = movilRedKey;
    }

    public String getExternalReference() {
        return this.externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getDocumentDate() {
        return this.documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getBank() {
        return this.bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getIdConsignment() {
        return this.idConsignment;
    }

    public void setIdConsignment(String idConsignment) {
        this.idConsignment = idConsignment;
    }

    public String getCorrelationId() {
        return this.correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String toString() {
        return "ConsignmentInDTO [idConsignment=" + this.idConsignment + ", client=" + this.client + ", amount=" + this.amount + ", bank=" + this.bank + ", movilRedKey=" + this.movilRedKey + ", externalReference=" + this.externalReference + ", documentDate=" + this.documentDate + ", regId=" + this.regId + "]";
    }
}

