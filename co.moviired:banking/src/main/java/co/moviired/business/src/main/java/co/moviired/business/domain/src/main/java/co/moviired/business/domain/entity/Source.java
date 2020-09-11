package co.moviired.business.domain.entity;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@Entity(name = "ws_source")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Source implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, length = 45)
    private String id;

    @Column(nullable = false, length = 100)
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
    private List<BillerSourceConnector> billerConnectors;


}
