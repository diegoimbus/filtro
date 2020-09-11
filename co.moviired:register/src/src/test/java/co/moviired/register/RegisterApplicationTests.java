package co.moviired.register;

import org.apache.http.util.Asserts;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RegisterApplicationTests {

    @Test
    void contextLoads() {
        Asserts.check(1 > 0, "Smoke Test");
    }

}

