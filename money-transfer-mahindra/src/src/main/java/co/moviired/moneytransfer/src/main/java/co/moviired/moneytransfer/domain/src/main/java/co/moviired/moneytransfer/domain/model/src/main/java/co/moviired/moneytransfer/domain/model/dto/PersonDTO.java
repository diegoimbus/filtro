package co.moviired.moneytransfer.domain.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PersonDTO implements Serializable {

    private String documentType;
    private String documentNumber;
    private String name;
    private String phoneNumber;
    private String email;
    private String typePerson;
    private String typeUser;

}

