package co.moviired.digitalcontent.business.domain.entity;

import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import co.moviired.digitalcontent.business.domain.enums.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hr_user")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User implements Serializable {

    private static final long serialVersionUID = 3567087174121099153L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private UserType type = UserType.GENERAL_USER;

    @NotBlank
    @Column(nullable = false)
    private String getraxUsername;

    @NotBlank
    @Column(nullable = false)
    private String getraxPassword;

    @NotBlank
    @Column(nullable = false)
    private String mahindraUsername;

    @NotBlank
    @Column(nullable = false)
    private String mahindraPassword;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private IncommConfig config;

}

