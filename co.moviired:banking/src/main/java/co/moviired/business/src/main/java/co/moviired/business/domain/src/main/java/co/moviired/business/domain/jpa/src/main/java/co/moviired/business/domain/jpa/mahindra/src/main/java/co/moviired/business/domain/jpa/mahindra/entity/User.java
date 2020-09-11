package co.moviired.business.domain.jpa.mahindra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode
@Entity(name = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private String id;

    @Column(name = "status")
    private String status;

    @Column(name = "agent_code")
    private String agentCode;

    @Column(name = "commercial_field_1")
    private String commercialField1;

    @Column(name = "commercial_field_2")
    private String commercialField2;

    @Column(name = "commercial_field_3")
    private String commercialField3;

    @Column(name = "commercial_field_4")
    private String commercialField4;

    @Column(name = "commercial_field_5")
    private String commercialField5;

    @Column(name = "commercial_field_6")
    private String commercialField6;

    @Column(name = "commercial_field_7")
    private String commercialField7;

    @Column(name = "commercial_field_8")
    private String commercialField8;

    @Column(name = "commercial_field_9")
    private String commercialField9;

    @Column(name = "commercial_field_10")
    private String commercialField10;

    @Column(name = "commercial_field_11")
    private String commercialField11;

    @Column(name = "msisdn")
    private String msisdn;

    public final void toPublic() {
        this.id = null;
        this.status = null;
        this.agentCode = null;
    }

}

