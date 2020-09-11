package co.moviired.digitalcontent.business.domain.entity;

import co.moviired.digitalcontent.business.domain.enums.BalanceModality;
import co.moviired.digitalcontent.business.domain.enums.Channel;
import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import co.moviired.digitalcontent.business.domain.enums.MigrationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
    @Column(nullable = false)
    private BalanceModality balanceModality = BalanceModality.GENERAL;

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

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    // Utils methods

    public final boolean isEnabled() {
        return (status.equals(GeneralStatus.ENABLED));
    }

    public final boolean isMigrated() {
        return (migration.equals(MigrationStatus.MIGRATED));
    }

}

