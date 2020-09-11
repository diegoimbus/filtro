package co.moviired.support.otp.model.entity;

import co.moviired.support.otp.helper.HashMapConverter;
import co.moviired.support.otp.model.enums.OtpState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "general_otp",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_otp", columnNames = {"component", "phone_number", "value"})
        },
        indexes = {
                @Index(name = "idx_otp", columnList = "component,phone_number,value"),
                @Index(name = "idx_otp_expiration", columnList = "expiration_date")
        }
)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Otp implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Builder.Default
    @Column(nullable = false)
    private String component = "OTHER";

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(nullable = false)
    private String value;

    @Builder.Default
    @Column(name = "creation_date", nullable = false)
    private Date creationDate = new Date();

    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    @Column(name = "expiration_lapse")
    private Integer expirationLapse;

    @Column(name = "validation_date")
    private Date validationDate;

    @Column(name = "modification_date")
    private Date modificationDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OtpState state = OtpState.PENDING;

    @Builder.Default
    @Column(name = "validation_attemps")
    private Integer validationAttemps = 0;

    @Builder.Default
    @Column(name = "created_by", nullable = false)
    private String createdBy = "support-otp";

    @Builder.Default
    @Column(name = "template_code", length = 4, nullable = false)
    private String templateCode = "0001";

    @Convert(converter = HashMapConverter.class)
    private Map<String, String> variables;

}

