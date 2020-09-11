package co.moviired.transpiler.integration.rest.parser;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.rest.parser.impl.*;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class RestParserFactory implements Serializable {

    private static final long serialVersionUID = 4411687968907774444L;

    private final TopUpParser topUpParser;
    private final CashOutParser cashOutParser;
    private final BillPayParser billPayParser;
    private final ValidateBillByRefenceParser validateBillByRefenceParser;
    private final ValidateBillEanParser validateBillEanParser;
    private final DigitalContentParser digitalContentParser;

    public RestParserFactory(TopUpParser ptopUpParser,
                             CashOutParser pcashOutParser,
                             BillPayParser pbillPayParser,
                             ValidateBillByRefenceParser pvalidateBillByRefenceParser,
                             ValidateBillEanParser pvalidateBillEanParser,
                             DigitalContentParser pdigitalContentParser) {
        super();
        this.topUpParser = ptopUpParser;
        this.cashOutParser = pcashOutParser;
        this.billPayParser = pbillPayParser;
        this.validateBillByRefenceParser = pvalidateBillByRefenceParser;
        this.validateBillEanParser = pvalidateBillEanParser;
        this.digitalContentParser = pdigitalContentParser;
    }

    public final IHermesParser getParser(OperationType operationType) throws ParseException {
        IHermesParser parser;

        switch (operationType) {
            case TOPUP:
                parser = this.topUpParser;
                break;

            case CASH_OUT:
                parser = this.cashOutParser;
                break;

            case VALIDATE_BILL_REFERENCE:
                parser = this.validateBillByRefenceParser;
                break;

            case VALIDATE_BILL_EAN:
                parser = this.validateBillEanParser;
                break;

            case BILL_PAY:
                parser = this.billPayParser;
                break;
            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                parser = this.digitalContentParser;
                break;

            default:
                throw new ParseException("Operación inválida");
        }

        return parser;
    }

}

