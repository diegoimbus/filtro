package co.moviired.auth.server.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/*
 * Copyright @2020. Moviired, SAS. Todos los derechos reservados.
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
@Table(name = "auth_mac_white_list")
public class MacWhiteList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "mac_address", nullable = false)
    private String mac;

    @Builder.Default
    @Column(name = "register_date", nullable = false)
    private Date registerDate = new Date();

    @Builder.Default
    @Column(name = "created_by", nullable = false)
    private String createdBy = "authentication";

}

