package co.moviired.acquisition.common.model.method;

import lombok.Data;

import java.util.Map;

@Data
public class ServicesRestContainer {

    private Map<String, Service> rest;
}

