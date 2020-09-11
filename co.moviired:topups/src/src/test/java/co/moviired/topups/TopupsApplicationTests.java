package co.moviired.topups;

import co.moviired.topups.cache.OperatorCache;
import co.moviired.topups.conf.MahindraExpDateProperties;
import co.moviired.topups.exception.ParseException;
import co.moviired.topups.mahindra.parser.impl.RechargeMahindraParser;
import co.moviired.topups.model.domain.Operator;
import co.moviired.topups.model.domain.repository.IOperatorRepository;
import co.moviired.topups.service.OperatorService;
import co.moviired.topups.service.TopupsService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class TopupsApplicationTests {

    private final String operatorErrorMessage = "Operador inválido";

    @Value(value = "${spring.messages.inactiveOperator}")
    private String operatorStatusInactiveMsg;

    @Value(value = "${spring.messages.suspendedOperator}")
    private String operatorStatusSuspendedMsg;

    @Autowired
    private MahindraExpDateProperties mahindraFormatter;

    @Autowired
    private IOperatorRepository operatorListRepo;

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private IOperatorRepository operatorRepository;

    @Autowired
    private TopupsService topupsService;

    @Autowired
    private RechargeMahindraParser mahindraParser;

    @MockBean(name = "wsOperatorCache")
    OperatorCache opCache;

    @Value("#{'${spring.application.operator.status.getOperatorsStatus}'.split(',')}")
    private List<Integer> operatorStatus;

    @Test
    public void findOperatorByEanCodeAndProductId() {
        String productCode = "154";
        String eanCode = "799366425076";
        Operator operator = operatorRepository.findByEanCodeAndProductCode(eanCode, productCode);
        assertNotNull("could not get object from database", operator);
    }

    @Test
    public void findOperatorByEanCode() {
        String eanCode = "799366425076";
        Operator operator = operatorRepository.findByEanCode(eanCode);
        assertNotNull("could not get object from database", operator);
    }

    @Test
    public void findOperatorByProdCodeAndType() {
        String productCode = "50029";
        int type = 1;
        Operator operator = operatorRepository.findByProductCodeAndType(productCode, type);
        assertNotNull("could not get object from database", operator);
    }

    @Test
    public void testAuthorizationHeaderSuccessful() {
        String authorization = "3176483081:1234";
        assertTrue("invalid regex matcher validation", topupsService.validateAuthorizationHeaderField(authorization));
    }

    @Test
    public void testAuthorizationHeaderFailed() {
        String authorization = "31764830811:123L";
        assertFalse("invalid regex matcher validation check for yaml regex", topupsService.validateAuthorizationHeaderField(authorization));
    }

    @Test
    public void testValidateOperatorActive() {
        Operator operator = getDummyOperator(1);
        try {
            boolean validateOperator = mahindraParser.validateOperator(operator);
            assertTrue("Invalid Active operator condition", validateOperator);
        } catch (ParseException e) {
            fail("Operator must be active");
        }
    }

    @Test
    public void testValidateOperatorInactive() {
        Operator operator = getDummyOperator(0);
        try {
            mahindraParser.validateOperator(operator);
        } catch (ParseException e) {
            assertEquals("", operatorStatusInactiveMsg, e.getMessage());
        }
    }

    @Test
    public void testValidateOperatorSuspended() {
        Operator operator = getDummyOperator(2);
        try {
            mahindraParser.validateOperator(operator);
        } catch (ParseException e) {
            assertEquals("Invalid Operator Suspended Status", operatorStatusSuspendedMsg, e.getMessage());
        }
    }

    @Test
    public void testValidateOperatorIsNull() {
        try {
            mahindraParser.validateOperator(null);
        } catch (ParseException e) {
            assertEquals("", operatorErrorMessage, e.getMessage());
        }
    }

    @Test
    public void testCache() {
        String eancode = "7707175322809";
        when(opCache.getOperatorByEanCodeAndProductCode(eancode, "1")).thenReturn(getCacheOperator(eancode));
        Operator operator = opCache.getOperatorByEanCodeAndProductCode(eancode, "1");
        assertNotNull(operator);
        assertEquals(eancode, operator.getEanCode());
    }

    @Test
    public void testDateFormmaterSuccessful() {
        String dateToFormatStr = "07/03/2019 16:40:24";
        String response = mahindraParser.formatMahindraDateToRechargeDate(this.getClass().getName() + ".testDateFormmaterSuccessful()", dateToFormatStr, mahindraFormatter);
        assertEquals("invalid Date formatted response", "2019-03-07 16:40:24", response);
    }

    @Test
    public void testDateFormmaterEmpty() {
        String dateToFormatStr = "";
        String response = mahindraParser.formatMahindraDateToRechargeDate(this.getClass().getName() + ".testDateFormmaterEmpty()", dateToFormatStr, mahindraFormatter);
        assertEquals("invalid Date formatted response", "", response);
    }


    @Test
    public void testDateFormmaterNull() {
        String dateToFormatStr = null;
        String response = mahindraParser.formatMahindraDateToRechargeDate(this.getClass().getName() + ".testDateFormmaterNull()", dateToFormatStr, mahindraFormatter);
        assertEquals("invalid Date formatted response", "", response);
    }

    @Test
    public void testGestors() {
        String authorizationcode = "0202_TestAuthCode";
        String authCode = mahindraParser.getGestorIdFromMahindraResponse(this.getClass().getName() + ".testGestors()", authorizationcode);
        assertEquals("check gestorId logic", "0202_", authCode);
    }

    @Test
    public void testGestorsEmpty() {
        String authorizationcode = "TestAuthCode2";
        String authCode = mahindraParser.getGestorIdFromMahindraResponse(this.getClass().getName() + ".testGestors()", authorizationcode);
        assertEquals("check gestorId logic", "", authCode);
    }


    @Test
    public void testGenericDateFormmaterSuccessful() {
        String dateToFormatStr = "2019-03-19 23:59:59";
        String response = mahindraParser.genericFormatDate(
                this.getClass().getName() + ".testGenericDateFormmaterSuccessful()", dateToFormatStr,
                "yyyy-MM-dd HH:mm:ss", "dd/MM/yyyy HH:mm:ss");
        assertEquals("invalid Date formatted response", "19/03/2019 23:59:59", response);
    }

    @Test
    public void testFormateComponentDate() {
        long componentDateLong = 1553030509918L;
        String dateFormatted = mahindraParser.getComponentDateAsString(this.getClass().getName() + ".testFormateComponentDate()", componentDateLong);
        assertEquals("invalid componentDate Format", "20190319162149.918", dateFormatted);
    }

    @Test
    public void testGetOperatorsById() {
        int operatorId = 29;
        List<Integer> status = new ArrayList<>();
        status.add(1);
        status.add(2);
        Pageable pageable = PageRequest.of(0, 100, Sort.by("id").descending());
        List<Operator> response = operatorListRepo.findAllByOperatorIdAndStatusIn(operatorId, status, pageable);
        assertNotNull("please validate jpa operation", response);
        assertTrue("please valdite Operator by Id 29 in database or check logic", !response.isEmpty());
        //response.forEach(opt -> {System.out.println(opt);});
    }

    @Test
    public void testGetOperatorsByIdInvalid() {
        int operatorId = -1;
        List<Integer> status = Arrays.asList(1, 2);
        Pageable pageable = PageRequest.of(0, 100);
        List<Operator> response = operatorListRepo.findAllByOperatorIdAndStatusIn(operatorId, status, pageable);
        assertNotNull("please validate jpa operation with id -1", response);
        assertTrue("please valdite Operator by Id -1 in database or check logic", response.isEmpty());

    }

    @Test
    public void testOperatorServiceObtainAllOperatorsByOperatorId() {
        Integer operatorId = 1;
        String logIdent = this.getClass().getName() + ".testOperatorServiceObtainAllOperatorsByOperatorId()";
        int page = 0;
        int blockSize = 11;
        List<Operator> response = operatorService.obtainAllOperatorsByOperatorId(operatorId, logIdent, page, blockSize, operatorStatus);
        System.out.println("Tamaño Rta ->" + response.size());
        //response.forEach(operador -> System.out.println(operador));
        assertTrue("array must not be empty", !response.isEmpty());
    }

    @Test
    public void testOperatorServiceObtainAllOperators() {
        String logIdent = this.getClass().getName() + ".testOperatorServiceObtainAllOperators()";
        int page = 0;
        int blockSize = 11;
        List<Operator> response = operatorService.obtainAllOperators(logIdent, page, blockSize, operatorStatus);
        System.out.println("Tamaño Rta ->" + response.size());
        assertTrue("array must not be empty", !response.isEmpty());
    }

    @Test
    public void testGetOpratorsByDistinctByOperatorId() {
        Integer operatorId = 1;
        List<Operator> operators = Arrays.asList(getCacheOperator("12345"));
        io.vavr.collection.List<Operator> response = operatorService.getOpratorsByDistinct(this.getClass().getName() + ".testGetOpratorsByDistinctByOperatorId()", operatorId, operators);
        response.asJava().forEach(operator -> System.out.println(operator));
        assertTrue("invalid filtering size, the array size must be 1!!", response.asJava().size() == 1);
    }

    @Test
    public void testGetOpratorsByDistinct() {
        Integer operatorId = null;
        Operator operatorDummy;
        List<Operator> operators = new ArrayList<>();
        operatorDummy = getCacheOperator("345657");
        operatorDummy.setOperatorId(2);
        operators.add(operatorDummy);
        operatorDummy = getCacheOperator("123476");
        operatorDummy.setOperatorId(3);
        operators.add(operatorDummy);
        operatorDummy = getCacheOperator("4567");
        operatorDummy.setOperatorId(4);
        operators.add(operatorDummy);
        operatorDummy = getCacheOperator("l34inu6o95");
        operatorDummy.setOperatorId(3);
        operators.add(operatorDummy);
        operatorDummy = getCacheOperator("3o98376n42");
        operatorDummy.setOperatorId(3);
        operators.add(operatorDummy);
        operatorDummy = getCacheOperator("903485n293");
        operatorDummy.setOperatorId(3);
        operators.add(operatorDummy);
        io.vavr.collection.List<Operator> response = operatorService.getOpratorsByDistinct(this.getClass().getName() + ".testGetOpratorsByDistinct()", operatorId, operators);
        response.asJava().forEach(operator -> System.out.println(operator));
        assertTrue("invalid filtering size, the array size must be 3!!", response.asJava().size() == 3);
    }

    @Test
    public void testGetOpratorsByDistinctEmpty() {
        Integer operatorId = 1;
        List<Operator> operators = new ArrayList<>();
        io.vavr.collection.List<Operator> response = operatorService.getOpratorsByDistinct(this.getClass().getName() + ".testGetOpratorsByDistinctEmpty()", operatorId, operators);
        response.asJava().forEach(operator -> System.out.println(operator));
        assertTrue("invalid filtering size, the array must be empty", response.asJava().isEmpty());
    }

    /*public void testObtainOperatorsByOperatorId() {
        String logIdent = this.getClass().getName() + ".testObtainOperatorsByOperatorId()";
        Mono<IOperatorResponse> response = operatorService.obtainOperatorsByOperatorId(1, logIdent);
        response.subscribe(result -> {
            OperatorIntegrationResponse rta = (OperatorIntegrationResponse) result;
            System.out.println("Response---->" + rta.toString());
            assertEquals("invalid result", "0", rta.getErrorType());
            assertEquals("invalid result", "00", rta.getErrorCode());

        });
    }*/

   /* @Test
    public void testObtainOperatorsAll() {
        String logIdent = this.getClass().getName() + ".testObtainOperatorsAll()";
        Mono<IOperatorResponse> response = operatorService.obtainOperatorsByOperatorId(null, logIdent);
        response.subscribe(result -> {
            OperatorIntegrationResponse rta = (OperatorIntegrationResponse) result;
            System.out.println("Response---->" + rta.toString());
            assertEquals("invalid result", "0", rta.getErrorType());
            assertEquals("invalid result", "00", rta.getErrorCode());

        });
    }*/

   /* @Test
    public void testObtainOperatorsEmpty() {
        String logIdent = this.getClass().getName() + ".testObtainOperatorsEmpty()";
        Mono<IOperatorResponse> response = operatorService.obtainOperatorsByOperatorId(100000, logIdent);
        response.subscribe(result -> {
            OperatorIntegrationResponse rta = (OperatorIntegrationResponse) result;
            System.out.println("Response---->" + rta.toString());
            assertEquals("invalid result", "0", rta.getErrorType());
            assertEquals("invalid result", "00", rta.getErrorCode());

        });
    }*/

    private Operator getCacheOperator(String eancode) {
        Operator op = new Operator();
        op.setId(999);
        op.setName("CacheOperator");
        op.setEanCode(eancode);
        op.setStatus(1);
        op.setProductCode("1");
        op.setOperatorId(1);
        return op;
    }

    private Operator getDummyOperator(Integer status) {
        return Operator.builder().environment("DEV")
                .microserviceRoot("http://192.168.29.50:34001/incomm/v1/sales/pines").microserviceUrl("topup")
                .minValue(30000).maxValue(30000).multiple(30000).name("Colombia Netflix DDP $30.000 COP").operatorId(19)
                .productCode("154").type(8).eanCode("799366425076").status(status).build();
    }
}

