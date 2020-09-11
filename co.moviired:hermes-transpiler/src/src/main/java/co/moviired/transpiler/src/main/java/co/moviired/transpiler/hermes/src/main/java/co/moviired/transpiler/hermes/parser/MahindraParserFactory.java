package co.moviired.transpiler.hermes.parser;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.hermes.parser.impl.*;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class MahindraParserFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final CashOutMahindraParser cashOutMahindraParser;
    private final TopUpMahindraParser topUpMahindraParser;
    private final BillPayMahindraParser billPayMahindraParser;
    private final ValidateBillByReferenceMahindraParser validateBillByReferenceMahindraParser;
    private final ValidateBillByEanMahindraParser validateBillByEanMahindraParser;
    private final DigitalContentMahindraParser digitalContentMahindraParser;

    public MahindraParserFactory(CashOutMahindraParser pcashOutMahindraParser,
                                 TopUpMahindraParser ptopUpMahindraParser,
                                 BillPayMahindraParser pbillPayMahindraParser,
                                 ValidateBillByReferenceMahindraParser pvalidateBillByReferenceMahindraParser,
                                 ValidateBillByEanMahindraParser pvalidateBillByEanMahindraParser,
                                 DigitalContentMahindraParser digitalContentMahindraParser) {
        super();
        this.cashOutMahindraParser = pcashOutMahindraParser;
        this.topUpMahindraParser = ptopUpMahindraParser;
        this.billPayMahindraParser = pbillPayMahindraParser;
        this.validateBillByReferenceMahindraParser = pvalidateBillByReferenceMahindraParser;
        this.validateBillByEanMahindraParser = pvalidateBillByEanMahindraParser;
        this.digitalContentMahindraParser = digitalContentMahindraParser;
    }

    public final IMahindraParser getParser(OperationType operationType) throws ParseException {
        IMahindraParser parser;

        switch (operationType) {
            case TOPUP:
                parser = this.topUpMahindraParser;
                break;

            case CASH_OUT:
                parser = this.cashOutMahindraParser;
                break;

            case BILL_PAY:
                parser = this.billPayMahindraParser;
                break;

            case VALIDATE_BILL_REFERENCE:
                parser = this.validateBillByReferenceMahindraParser;
                break;

            case VALIDATE_BILL_EAN:
                parser = this.validateBillByEanMahindraParser;
                break;

            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                parser = this.digitalContentMahindraParser;
                break;

            default:
                throw new ParseException("Operación inválida");
        }

        return parser;
    }
}

