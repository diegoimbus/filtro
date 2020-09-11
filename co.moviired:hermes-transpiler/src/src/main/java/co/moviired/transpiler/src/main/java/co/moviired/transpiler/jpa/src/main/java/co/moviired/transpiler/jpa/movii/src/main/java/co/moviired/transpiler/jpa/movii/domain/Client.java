package co.moviired.transpiler.jpa.movii.domain;

import co.moviired.transpiler.jpa.movii.domain.enums.BalanceModality;
import co.moviired.transpiler.jpa.movii.domain.enums.Channel;
import co.moviired.transpiler.jpa.movii.domain.enums.GeneralStatus;
import co.moviired.transpiler.jpa.movii.domain.enums.MigrationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hr_client")
public class Client implements Serializable {

    private static final long serialVersionUID = -181405643972672692L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "time_zone")
    private String timeZone;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private Channel channel = Channel.TAT;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private MigrationStatus migration = MigrationStatus.PENDING;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(name = "balance_modality", nullable = false)
    private BalanceModality balanceModality = BalanceModality.GENERAL;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    @Column(name = "last_modified_user")
    private String lastModifiedUser;

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

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<ClientProduct> products = new ArrayList<>();

}

