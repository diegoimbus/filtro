package co.moviired.business.domain.jpa.getrax.entity;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KEYTYPE")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KEYTYPE implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;
    private static final int NAME_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "KETY_ID")
    private Integer ketyId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "KETY_NAME")
    private String ketyName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "KETY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ketyDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "KETY_REGISTEREDBY")
    private int ketyRegisteredby;
}

