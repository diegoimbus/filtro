package co.moviired.transaction;

import co.moviired.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = TransactionApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionServiceTest {

    private static final String OK = "OK";

    @Autowired
    TransactionService transactionService;

    @Test
    void getPing() {
        log.info("**************  Iniciando test ping  **************");
        assertEquals(OK, transactionService.ping());
        log.info("************** Finalizando test ping **************");
    }

    @Test
    void getServiceTypes() {
        log.info("**************  Iniciando test getServiceTypes  **************");
        String authorization = "3500000000:1111";
        assertEquals(OK, transactionService.getServiceTypes(Mono.just(authorization)).block().getResponseType());
        log.info("************** Finalizando test getServiceTypes **************");
    }

}

