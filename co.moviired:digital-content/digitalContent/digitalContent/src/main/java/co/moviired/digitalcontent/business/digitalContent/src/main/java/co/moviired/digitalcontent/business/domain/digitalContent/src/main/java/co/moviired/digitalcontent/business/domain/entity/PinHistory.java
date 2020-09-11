package co.moviired.digitalcontent.business.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "ic_pin_history")
public class PinHistory implements Serializable {

    private static final long serialVersionUID = 3567087174121099153L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column
    private String correlativoId;

    @Column
    private String transferId;

    @Column
    private String phoneNumber;

    @Column
    private boolean sendResponse;

    @Column
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String pin;

    @NotBlank
    @Column(nullable = false)
    private String authorizationCode;

    @NotNull
    @CreatedDate
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

}

