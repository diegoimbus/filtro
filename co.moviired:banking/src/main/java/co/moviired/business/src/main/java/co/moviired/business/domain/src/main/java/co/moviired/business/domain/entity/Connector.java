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
@Entity(name = "ws_connector")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Connector implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String description;

    @Column(name = "url_connector", nullable = false)
    private String urlConnector;

    @Column(name = "query_method", nullable = false)
    private String queryMethod;

    @Column(name = "confirm_method", nullable = false)
    private String confirmMethod;

    @Column(name = "gestor_id", nullable = false)
    private String gestorId;

    @JsonIgnore
    @OneToMany(mappedBy = "connector", cascade = CascadeType.ALL)
    private List<BillerSourceConnector> billerConnectors;

}
