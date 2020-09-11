package co.moviired.support;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.enumeration.CryptoSpec;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.conf.GlobalProperties;
import co.moviired.support.conf.SupportOtpProperties;
import co.moviired.support.domain.dto.enums.Gender;
import co.moviired.support.domain.dto.enums.Status;
import co.moviired.support.domain.entity.User;
import co.moviired.support.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@SpringBootApplication
@Component
@EnableConfigurationProperties(value = {SupportOtpProperties.class})
public class SupportUsersApplication implements ApplicationListener<ContextRefreshedEvent> {

    private static final String LOG_LINE = "-------------------------------------------";

    private final GlobalProperties config;
    private final IUserRepository userRepository;

    public SupportUsersApplication(GlobalProperties pglobalProperties, IUserRepository puserRepository) {
        super();
        this.config = pglobalProperties;
        this.userRepository = puserRepository;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SupportUsersApplication.class);
        app.run(args);
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (event.getApplicationContext().getId() != null) {

                String logFormatted2 = "{} {}";
                // Evidenciar en el LOG el inicio correcto de los servicios
                log.info("");
                log.info(LOG_LINE);
                log.info(logFormatted2, config.getApplicationName(), " application started ");
                log.info(logFormatted2, "Port: ", config.getRestPort());
                log.info(logFormatted2, "Version: ", config.getApplicationVersion());
                log.info("Launched [OK]");
                createUserAdmin();

                createUserRisk();

                log.info(LOG_LINE);
                log.info("");
            }

        } catch (Exception | ParsingException e) {
            log.error(e.getMessage(), e);
        }
    }


    private void createUserAdmin() {
        Optional<User> oUser = this.userRepository.findFirstByMsisdn(this.config.getMsisdn());
        if (!oUser.isPresent()) {

            User user = new User();
            log.info("No se encuentra el usuario ADMON... Se procede a crear un usuario administrador portal");
            user.setFirstName(this.config.getFirstName());
            user.setMsisdn(this.config.getMsisdn());
            user.setCellphone(this.config.getCellPhone());
            user.setUserType(this.config.getUserType());
            user.setDob(this.config.getDob());
            user.setIdtype(this.config.getIdType());
            user.setAgentCode(this.config.getAgentCode());
            user.setIdno(this.config.getIdno());
            user.setEmail(this.config.getEmail());
            user.setGender(Gender.valueOf(this.config.getGender()));
            user.setChangePasswordRequired("NO_REQUIRED");
            user.setStatus(Status.valueOf(this.config.getStatus()));
            user.setMpin(this.config.getMpin());
            user.setRegistrationDate(new Date());
            user.setSign(user.hashCode());
            this.userRepository.save(user);
            log.info("Usuario creado.");
        }
    }


    private void createUserRisk() throws ParsingException {
        Optional<User> oUser = this.userRepository.findFirstByMsisdn(this.config.getRiskMsisdn());
        if (!oUser.isPresent()) {

            log.info("No encuentra el usuario RISK... Se procede a crear un usuario de riesgo");
            User user = new User();
            user.setFirstName(this.config.getRiskFirstName());
            user.setMsisdn(this.config.getRiskMsisdn());
            user.setCellphone(this.config.getRiskCellPhone());
            user.setUserType(this.config.getRiskUserType());
            user.setDob(this.config.getRiskDob());
            user.setIdtype(this.config.getRiskIdType());
            user.setAgentCode(this.config.getRiskAgentCode());
            user.setIdno(this.config.getRiskIdno());
            user.setEmail(this.config.getRiskEmail());
            user.setGender(Gender.valueOf(this.config.getRiskGender()));
            user.setChangePasswordRequired("NO_REQUIRED");
            user.setStatus(Status.valueOf(this.config.getRiskStatus()));
            user.setMpin(this.config.getRiskMpin());
            user.setRegistrationDate(new Date());
            user.setSign(user.hashCode());
            this.userRepository.save(user);

            log.info("Usuario creado.");
        }
    }

    @Bean("supportOtpConnector")
    public ReactiveConnector supportOtpConnector(SupportOtpProperties supportOTPProperties) {

        return new ReactiveConnector(supportOTPProperties.getUrl(), supportOTPProperties.getConnectionTimeout(), supportOTPProperties.getReadTimeout());
    }


    @Bean("cryptoHelper")
    public CryptoHelper cryptoHelper(Environment environment) {
        return new CryptoHelper(StandardCharsets.UTF_8, CryptoSpec.AES_CBC,
                environment.getRequiredProperty("crypt.key"),
                environment.getRequiredProperty("crypt.initializationVector"));
    }

}



