package co.moviired.acquisition;

import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.common.model.dto.ResponseStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static co.moviired.acquisition.common.util.StatusCodesHelper.SUCCESS_CODE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AcquisitionApplicationTests {

    @Test
    void contextLoads() {
        IComponentDTO myClass = new IComponentDTO();
        myClass.setStatus(new ResponseStatus());
        myClass.getStatus().setCode(SUCCESS_CODE);
        assertTrue(myClass.isSuccessResponse());
    }
}

