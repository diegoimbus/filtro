package co.moviired.business.domain.jpa.movii.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "ws_biller_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillerCategory implements Serializable {

    private static final long serialVersionUID = 9123981123L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "logo")
    private String logo;

    @OneToMany(mappedBy = "billerCategory")
    private List<SellerCategory> listSellerCategory;

    public final void toPublic() {
        this.listSellerCategory = null;
    }

}

