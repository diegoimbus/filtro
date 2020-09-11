package co.moviired.digitalcontent.business.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ic_incomm_config")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IncommConfig implements Serializable {

    private static final long serialVersionUID = 3567087174121099153L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Column
    private boolean sendMail = Boolean.FALSE;

    @Builder.Default
    @Column
    private boolean sendSMS = Boolean.FALSE;

    @Builder.Default
    @Column
    private boolean sendPin = Boolean.FALSE;

    @Builder.Default
    @Column
    private boolean encryptPin = Boolean.FALSE;

    @Builder.Default
    @Column
    private String templateSms = "Este el pin : #PIN#";

    @Builder.Default
    @Column
    private String templateMail = "Este el pin : #PIN#";

    @NotNull
    @Column(name = "encryption_key")
    private String encryptionKey;

    @Builder.Default
    @Column
    private boolean useNewEncryptionAlgorithm = Boolean.FALSE;
}

