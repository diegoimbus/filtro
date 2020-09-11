package co.moviired.microservice.conf;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BankProductsProperties {

    @Value("${banksProducts.bbva.queryWithdrawal}")
    private String productIdBbvaQueryWithDrawal;

    @Value("${banksProducts.bbva.withdrawal}")
    private String productIdBvvaWithdrawal;

    @Value("${banksProducts.agrario.deposit}")
    private String productIdAgrarioDeposit;

    @Value("${banksProducts.agrario.withdrawal}")
    private String productIdAgrarioWithdrawal;

    @Value("${banksProducts.agrario.queryObligations}")
    private String productIdAgrarioQueryObligations;

    @Value("${banksProducts.agrario.payObligations}")
    private String productIdAgrarioPayObligations;


    @Value("${banksProducts.agrario.prefixPayObligations}")
    private String prefixAgrarioPayObligations;


}

