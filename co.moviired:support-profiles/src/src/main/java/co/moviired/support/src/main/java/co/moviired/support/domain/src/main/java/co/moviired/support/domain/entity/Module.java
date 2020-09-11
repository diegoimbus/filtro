package co.moviired.support.domain.entity;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

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
@Table(name = "prf_module")
public class Module implements Serializable {

    private static final long serialVersionUID = -2381315378760910845L;

    private static final int NAME_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String moduleName;

    @Column(name = "module_description", nullable = false, length = NAME_LENGTH)
    private String moduleDescription;


    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "prf_module_operation",
            joinColumns = @JoinColumn(name = "module_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "operation_id", referencedColumnName = "id"))
    private List<Operation> operations;

}


