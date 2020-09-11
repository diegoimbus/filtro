package co.movii.auth.server.domain.entity;

import co.movii.auth.server.domain.enums.ClientType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "auth_register_login")
public class AuthRegisterLogin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "device", nullable = false)
    private String device;

    @Builder.Default
    @Column(name = "login_date", nullable = false)
    private Date loginDate = new Date();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false)
    private ClientType clientType = ClientType.SUBSCRIBER;

}

