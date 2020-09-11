package co.moviired.support.domain.entity;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.support.domain.enums.GeneralStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "prf_operation")
public class Operation implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private static final int NAME_LENGTH = 500;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String operationName;

    @Column(name = "operation_description", nullable = false, length = NAME_LENGTH)
    private String operationDescription;

    @Column(name = "operation_url", nullable = false)
    private String operationUrl;

    @Enumerated
    @Builder.Default
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

    @JsonIgnore
    @ManyToMany(mappedBy = "operations")
    private List<Profile> profiles;

    @JsonIgnore
    @ManyToMany(mappedBy = "operations")
    private List<Module> modules;


    public void toPublic(){
        this.modules = null;
        this.profiles = null;
    }
}

