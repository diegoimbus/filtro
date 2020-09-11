package co.moviired.supportp2pvalidatortransaction.common.model.method;

import lombok.Data;

import java.util.HashMap;

@Data
public class ServicesRestContainer {

    private HashMap<String, Service> rest;
}

