package co.moviired.register.domain.model.entity;

import co.moviired.register.domain.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static co.moviired.register.helper.ConstantsHelper.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = MUNICIPALITY_TABLE)
public class Municipality extends BaseModel implements Serializable {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = MUNICIPALITY_PARAMETER_ID, length = 11, nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = MUNICIPALITY_DEPARTMENT, nullable = false)
    @JsonIgnore
    private Department department;

    @Column(name = MUNICIPALITY_PARAMETER_NAME, length = 100, nullable = false)
    private String name;

    @Column(name = MUNICIPALITY_PARAMETER_CODE, length = 10, nullable = false, unique = true)
    private String daneCode;

}

