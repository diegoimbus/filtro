package co.moviired.transpiler.jpa.getrax.domain;

/*
 * Copyright @2019. SBD, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-02-14
 * @since 1.0.7
 */

import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PRODUCTO")
public class ProductGetrax implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private static final int CODE_LENGTH = 20;
    private static final int NAME_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROD_ID", nullable = false, unique = true)
    private Integer productCode;

    @Column(name = "EANCODE", length = CODE_LENGTH)
    private String code;

    @Column(name = "OPER_ID")
    private String id;

    @Column(name = "PROD_DESCRIPCION", length = NAME_LENGTH)
    private String name;

    @Enumerated
    @Builder.Default
    @Column(name = "ESTADO", nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

}

