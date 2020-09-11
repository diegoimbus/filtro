package co.moviired.acquisition.model.entity;

import co.moviired.acquisition.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

import static co.moviired.acquisition.common.util.ConstantsHelper.COLUMN_DEFINITION_TINYINT;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class StatusAndDateLog extends IModel {

    @JsonIgnore
    @Column(nullable = false, columnDefinition = COLUMN_DEFINITION_TINYINT)
    private boolean status;

    @JsonIgnore
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

