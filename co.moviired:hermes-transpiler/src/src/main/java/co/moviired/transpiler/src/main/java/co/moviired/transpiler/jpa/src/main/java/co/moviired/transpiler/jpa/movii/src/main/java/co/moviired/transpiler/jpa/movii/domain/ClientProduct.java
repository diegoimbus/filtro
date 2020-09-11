package co.moviired.transpiler.jpa.movii.domain;

import co.moviired.transpiler.jpa.movii.domain.enums.MigrationStatus;
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
@Table(name = "hr_client_product")
public class ClientProduct implements Serializable {

    private static final long serialVersionUID = -8922898386190611613L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @CreatedBy
    @Builder.Default
    @Column(name = "created_user", nullable = false)
    private String createdUser = "admin@movii.co";

    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @NotNull
    @Builder.Default
    @Enumerated
    @Column(nullable = false)
    private MigrationStatus migration = MigrationStatus.PENDING;

    @LastModifiedBy
    @Column(name = "last_modified_user")
    private String lastModifiedUser;

    @NotNull
    @CreatedDate
    @Builder.Default
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    // Utils methods

    public final boolean isMigrated() {
        return (migration.equals(MigrationStatus.MIGRATED));
    }

}

