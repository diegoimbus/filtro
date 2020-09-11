package co.moviired.support.domain.entity;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.enums.GeneralStatus;
import co.moviired.support.domain.enums.Origin;
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
import java.util.List;


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prf_profile")
public class Profile implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private static final int NAME_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String profileName;

    @Column(name = "profile_description", nullable = false, length = NAME_LENGTH)
    private String profileDescription;

    @NotBlank
    @CreatedBy
    @Builder.Default
    @Column(name = "created_user", nullable = false)
    private String createdUser = "admin@movii.co";

    @NotNull
    @CreatedDate
    @Builder.Default
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();


    @LastModifiedBy
    @Column(name = "last_modified_user")
    private String lastModifiedUser;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Enumerated
    @Builder.Default
    @Column(nullable = false)
    private GeneralStatus enableDelete = GeneralStatus.DISABLED;

    @Enumerated
    @Builder.Default
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "prf_profile_operation",
            joinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "operation_id", referencedColumnName = "id"))
    private List<Operation> operations;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false)
    private Origin origin;
}


