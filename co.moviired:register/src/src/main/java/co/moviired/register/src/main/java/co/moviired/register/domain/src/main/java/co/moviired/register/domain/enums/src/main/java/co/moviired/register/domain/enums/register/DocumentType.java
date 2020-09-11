package co.moviired.register.domain.enums.register;

import co.moviired.register.helper.ConstantsHelper;

import java.util.Arrays;

public enum DocumentType {

    CC(ConstantsHelper.CC),
    CE(ConstantsHelper.CE),
    TI(ConstantsHelper.TI),
    PA(ConstantsHelper.PA),
    PEP(ConstantsHelper.PEP),
    PAS(ConstantsHelper.PA),
    UNKNOWN(null);

    private String documentToUse;

    DocumentType(String pDocumentToUse) {
        this.documentToUse = pDocumentToUse;
    }

    public static DocumentType getByDocumentToUse(String documentToUseI) {
        return Arrays.stream(values()).filter(value -> value.getDocumentToUse().equals(documentToUseI)).findFirst().orElse(UNKNOWN);
    }

    public String getDocumentToUse() {
        return documentToUse;
    }
}

