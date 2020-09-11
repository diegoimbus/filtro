package co.moviired.moneytransfer.domain.entity.redshift;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "MTX_TRANSACTION_HEADER", schema = "MAHINDRA")
public class Header {

    @Id
    @Column(name = "transfer_id")
    private String transferId;

    @Column(name = "transfer_status")
    private String transferStatus;

    @Column(name = "remarks")
    private String remarks;

}

