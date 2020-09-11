package co.moviired.transpiler.jpa.movii.domain;

import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import co.moviired.transpiler.jpa.movii.domain.enums.UserType;
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
@Table(name = "hr_user")
public class User implements Serializable {

    private static final long serialVersionUID = 3567087174121099153L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "user")
    private String username;

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
    @Column(name = "getrax_username", nullable = false, unique = true)
    private String getraxUsername;

    @NotBlank
    @Column(name = "getrax_password", nullable = false)
    private String getraxPassword;

    @LastModifiedBy
    @Column(name = "last_modified_user")
    private String lastModifiedUser;

    @NotBlank
    @Column(name = "mahindra_username", nullable = false)
    private String mahindraUsername;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @NotNull
    @CreatedDate
    @Builder.Default
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @NotBlank
    @CreatedBy
    @Builder.Default
    @Column(name = "created_user", nullable = false)
    private String createdUser = "admin@movii.co";

    @NotBlank
    @Column(name = "mahindra_password", nullable = false)
    private String mahindraPassword;

}

