package co.moviired.supportaudit.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(value = "audit")
public class Audit implements Serializable {

    @Id
    private String id;
    private String correlationId;
    private String username;
    private String operation;
    private Map<String, String> operationDetail;
    private String operationText;
    @Builder.Default
    private Date date = new Date();
}

