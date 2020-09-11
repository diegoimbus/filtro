package com.moviired.model.entities.moviiregister;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;

import java.io.Serializable;

import static com.moviired.helper.Constant.REGISTER_TABLE_PENDING_USER;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = REGISTER_TABLE_PENDING_USER)
public class PendingUser implements Serializable {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 11, nullable = false)
    private Integer id;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "status")
    private boolean status;

    @Column(name = "altered")
    private boolean altered;

    @Column(name = "process_type")
    @Enumerated(EnumType.STRING)
    private ProcessType processType;
    public enum ProcessType {
        NORMAL_REGISTRATION,
        SUBSIDY_REGISTRATION
    }
}
