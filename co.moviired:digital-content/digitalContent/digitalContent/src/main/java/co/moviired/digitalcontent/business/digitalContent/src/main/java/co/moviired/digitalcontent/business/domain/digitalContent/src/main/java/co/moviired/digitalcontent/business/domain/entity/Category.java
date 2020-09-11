package co.moviired.digitalcontent.business.domain.entity;

import co.moviired.digitalcontent.business.domain.enums.GeneralStatus;
import co.moviired.digitalcontent.business.domain.enums.SellerChannel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ws_category")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Category implements Serializable {

    private static final long serialVersionUID = -181405643972672692L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column
    private String productImage;

    @Enumerated
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.ENABLED;

    @CreatedBy
    @Column(nullable = false)
    private String createdUser = "admin@moviired.co";

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @LastModifiedBy
    @Column
    private String lastModifiedUser;

    @LastModifiedDate
    @Column
    private LocalDateTime lastModifiedDate;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ws_category_seller", joinColumns = @JoinColumn(name = "category_id"),
            uniqueConstraints = {@UniqueConstraint(name = "UQ_category_seller", columnNames = {"category_id", "sellers"})})
    private Set<SellerChannel> sellers = new HashSet<>();

    public final void toPublic() {
        this.createdDate = null;
        this.createdUser = null;
        this.lastModifiedDate = null;
        this.lastModifiedUser = null;
        this.sellers = new HashSet<>();

        // Productos
        if (this.products != null && !this.products.isEmpty()) {
            for (Product p : this.products) {
                p.toPublic();
            }
        }
    }

}

