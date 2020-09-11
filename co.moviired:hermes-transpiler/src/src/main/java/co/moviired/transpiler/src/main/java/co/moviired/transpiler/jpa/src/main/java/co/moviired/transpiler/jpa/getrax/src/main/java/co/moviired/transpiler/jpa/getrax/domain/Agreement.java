package co.moviired.transpiler.jpa.getrax.domain;

import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/*
 * Copyright @2019. SBD, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-02-14
 * @since 1.0.7
 */

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CONVENIO")
public class Agreement implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private static final int CODE_LENGTH = 20;
    private static final int NAME_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conv_id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "conv_codigo", length = CODE_LENGTH)
    private String code;

    @Column(name = "conv_nombre", length = NAME_LENGTH)
    private String name;

    @Enumerated
    @Builder.Default
    @Column(name = "conv_estado", nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;
}


