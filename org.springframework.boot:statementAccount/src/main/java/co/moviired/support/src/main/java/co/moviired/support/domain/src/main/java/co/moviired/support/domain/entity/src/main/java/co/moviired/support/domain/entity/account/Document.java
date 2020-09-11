package co.moviired.support.domain.entity.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "extract_document")
public class Document implements Serializable {

    private static final int LENGTH = 11;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = LENGTH, nullable = false, unique = true)
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

    public Document(Document document) {
        this.phoneNumber = document.phoneNumber;
        this.year = document.year;
        this.month = document.month;
        this.creationDate = document.creationDate;
        this.token = document.token;
        this.signature = document.signature;
        this.altered = document.altered;
        this.type = document.type;
    }

}

