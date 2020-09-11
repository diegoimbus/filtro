package co.moviired.digitalcontent.business.domain.entity;

import co.moviired.digitalcontent.business.domain.enums.BalanceModality;
import co.moviired.digitalcontent.business.domain.enums.Channel;
import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import co.moviired.digitalcontent.business.domain.enums.MigrationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hr_client")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

}

