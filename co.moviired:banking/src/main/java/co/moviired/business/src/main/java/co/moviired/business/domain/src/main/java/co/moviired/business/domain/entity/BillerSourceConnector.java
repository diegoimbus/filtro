package co.moviired.business.domain.entity;

import co.moviired.business.domain.jpa.movii.entity.Biller;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "ws_biller_source_connector",
        uniqueConstraints = {@UniqueConstraint(name = "UQ_BILLER_SOURCE_CONNECTOR", columnNames = {"id_biller", "source", "id_connector"})},
        indexes = {@Index(name = "IDX_BILLER_SOURCE_CONNECTOR", columnList = "id_biller,source,id_connector")}
)
public class BillerSourceConnector implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_biller", nullable = false)
    private Biller biller;

    @ManyToOne
    @JoinColumn(name = "source", nullable = false)
    private Source source;

    @ManyToOne
    @JoinColumn(name = "id_connector", nullable = false)
    private Connector connector;

}
