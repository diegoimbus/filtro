package co.moviired.digitalcontent.business.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ic_conciliacion")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Conciliacion implements Serializable {

    private static final long serialVersionUID = 3567087174121099153L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    //Cliente --> authentication
    @Column
    private String cliente;

    // ID  Transaccion digitalContent
    @Column
    private String correlationId;

    // ID  Transaccion digitalContent generado
    @Column
    private String idTransacion;

    // ID  Transaccion digitalContent de la transaccion a reversar
    @Column
    private String correlationIdR;

    // ID  Transaccion mahindra
    @Column
    private String transferId;

    // ID  Transaccion mahindra del reverso
    @Column(name = "transferId_revert")
    private String transferIdRevert;

    // Fecha de la petición
    @NotNull
    @CreatedDate
    @Builder.Default
    @Column
    private LocalDateTime createdDate = LocalDateTime.now();

    // Fecha de mahindra
    private LocalDateTime transaccionDate;

    @Column(name = "status_ic")
    private String statusIc;

    // Mensaje por si existe algun error
    @Column(name = "message_ic")
    private String messageIc;

    @Column(name = "status_mh")
    private String statusMh;

    // Mensaje por si existe algun error
    @Column(name = "message_mh")
    private String messageMh;

}

