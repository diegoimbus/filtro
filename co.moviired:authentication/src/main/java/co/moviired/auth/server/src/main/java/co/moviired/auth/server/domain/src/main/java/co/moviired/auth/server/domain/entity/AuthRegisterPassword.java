package co.moviired.auth.server.domain.entity;
/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "auth_register_password")
public class AuthRegisterPassword implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Builder.Default
    @Column(name = "change_password_date", nullable = false)
    private Date changePasswordDate = new Date();

    @Column(name = "return_code", nullable = false)
    private String returnCode;


}



