package com.moviired.model.entities.mahindra;


import com.moviired.model.dto.BankDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author carlossaul.ramirez
 * @category srv-cash
 */
@Entity(name = "MBK_BANK_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SqlResultSetMapping(name = "return_bankList", classes = @ConstructorResult(targetClass = BankDetailDTO.class, columns = {
        @ColumnResult(name = "bankId"),
        @ColumnResult(name = "bankName"),
}))
public class MbkBankDetail {

    @Id
    @Column(name = "bank_id")
    private String bankId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_pool_account_no")
    private String bankPoolAccountNo;

    @Column(name = "pool_account_type")
    private String poolAccountType;

    @Column(name = "sn")
    private Integer sn;

    @Column(name = "cbs_type")
    private String cbsType;

    @Column(name = "provider_id")
    private Integer providerId;
}

