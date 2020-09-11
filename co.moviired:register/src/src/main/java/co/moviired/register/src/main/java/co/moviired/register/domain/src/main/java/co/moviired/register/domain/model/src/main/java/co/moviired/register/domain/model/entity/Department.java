package co.moviired.register.domain.model.entity;

import co.moviired.register.domain.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static co.moviired.register.helper.ConstantsHelper.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = DEPARTMENT_TABLE, indexes = @Index(name = "idx_dept_name", columnList = DEPARTMENT_PARAMETER_NAME))
public class Department extends BaseModel implements Serializable {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = DEPARTMENT_PARAMETER_ID, length = 11, nullable = false)
    private Integer id;

    @Column(name = DEPARTMENT_PARAMETER_NAME, length = 100, nullable = false, unique = true)
    private String name;

    @OneToMany(
            mappedBy = "department",
            fetch = FetchType.EAGER,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<Municipality> municipalities;

}

