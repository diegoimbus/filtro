package co.moviired.support.domain.dto;

import co.moviired.support.domain.entity.redshift.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class GradeDTO {
    private Integer id;

    private String grade;

    public Grade toEntity() {
        Grade gradeEntity = new Grade();
        gradeEntity.setId(this.id);
        gradeEntity.setGrado(this.grade);

        return gradeEntity;
    }
}

