import co.moviired.moneytransfer.MoneyTransferApplication;
import co.moviired.moneytransfer.helper.ConstanHelper;
import co.moviired.moneytransfer.service.MoneyTransferService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = MoneyTransferApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneyTransferServiceTest {

    @Autowired
    MoneyTransferService moneyTransferService;

    @Test
    void getPing() {
        log.info("**************  Iniciando test getPing  **************");
        assertEquals(ConstanHelper.SUCCESS_CODE_00, moneyTransferService.getPing().block().getResponseCode());
        log.info("************** Finalizando test getPing **************");
    }



}
