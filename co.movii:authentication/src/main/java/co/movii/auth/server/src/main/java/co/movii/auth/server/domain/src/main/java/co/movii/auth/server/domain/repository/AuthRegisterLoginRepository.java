package co.movii.auth.server.domain.repository;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.movii.auth.server.domain.entity.AuthRegisterLogin;
import co.movii.auth.server.domain.enums.ClientType;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Date;

public interface AuthRegisterLoginRepository extends CrudRepository<AuthRegisterLogin, Long>, Serializable {

    AuthRegisterLogin findFirstByPhoneNumberOrderByLoginDateDesc(String phoneNumber);

    AuthRegisterLogin findFirstByPhoneNumberAndClientTypeOrderByLoginDateDesc(String phoneNumber, ClientType clientType);

    int countAllByPhoneNumberAndClientTypeAndLoginDateBetween(String phoneNumber, ClientType clientType, Date loginDate, Date loginDate2);

}

