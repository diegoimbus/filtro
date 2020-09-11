package co.moviired.business.provider.bankingswitch.response;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */


import java.io.Serializable;

@lombok.Data
public class Obligation implements Serializable {

    private String accountHolder;
    private String valuePartialPayment;
    private String date;
    private String valueToPay;
    private String referenceNumber;
}


