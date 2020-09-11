package co.moviired.digitalcontent.business.domain.entity;

import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import co.moviired.digitalcontent.business.domain.enums.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "hr_user")
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

    @NotBlank
    @CreatedBy
    @Builder.Default
    @Column(nullable = false)
    private String createdUser = "admin@moviired.co";

    @NotNull
    @CreatedDate
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedBy
    @Column
    private String lastModifiedUser;

    @LastModifiedDate
    @Column
    private LocalDateTime lastModifiedDate;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private IncommConfig config;

    // Utils methods

    public final boolean isEnabled() {
        return (status.equals(GeneralStatus.ENABLED));
    }

    public final boolean isBalanceUser() {
        return (type.equals(UserType.BALANCE_USER));
    }

}

