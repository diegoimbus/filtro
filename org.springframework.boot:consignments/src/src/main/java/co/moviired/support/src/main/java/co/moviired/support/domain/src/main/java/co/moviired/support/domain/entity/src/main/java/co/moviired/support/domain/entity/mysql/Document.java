package co.moviired.support.domain.entity.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "extract_document")
public class Document implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 11, nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String token;

    @JsonIgnore
    @Column(nullable = false)
    private String signature;

    @JsonIgnore
    @Column(nullable = false)
    private boolean altered;

    @Column(nullable = false)
    private String type;

}

