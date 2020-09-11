package co.moviired.topups.model.domain;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "ws_subtype_operator")
public class SubtypeOperator {

    @Id
    private Integer id;

    private String subtype;

    @JsonIgnore
    @OneToMany(mappedBy = "subType", fetch = FetchType.LAZY)
    private List<Operator> operators;
}

