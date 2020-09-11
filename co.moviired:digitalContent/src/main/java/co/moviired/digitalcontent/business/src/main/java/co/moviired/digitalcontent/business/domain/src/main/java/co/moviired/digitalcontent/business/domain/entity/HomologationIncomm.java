package co.moviired.digitalcontent.business.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ic_homologation_incomm_code")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HomologationIncomm implements Serializable {

    private static final long serialVersionUID = -8936285757583036124L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "network", nullable = false, unique = true)
    private String network;

    @Column(name = "incomm_code", nullable = false)
    private String incommCode;

}

