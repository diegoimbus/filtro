package co.moviired.business.domain.jpa.getrax.entity;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Builder
@ToString
@Table(name = "KEYINFO")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KEYINFO implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "KEIN_ID")
    private Integer keinId;
    @Size(max = 8)
    @Column(name = "KEIN_HEADER")
    private String keinHeader;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 48)
    @Column(name = "KEIN_CRYPTOGRAM")
    private String keinCryptogram;
    @Size(max = 16)
    @Column(name = "KEIN_MAC")
    private String keinMac;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "KEIN_CHECKSUM")
    private String keinChecksum;
    @Basic(optional = false)
    @NotNull
    @Column(name = "KEIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date keinDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "KEIN_REGISTEREDBY")
    private int keinRegisteredby;
    @Column(name = "KEIN_ISIMPORTKEY")
    private Boolean keinIsimportkey;
    // @JoinColumn(name = "KEIN_KETY_ID", referencedColumnName = "KETY_ID")
    // @ManyToOne(optional = false)
    @Column(name = "KEIN_KETY_ID")
    private KEYTYPE keinKetyId;

}

