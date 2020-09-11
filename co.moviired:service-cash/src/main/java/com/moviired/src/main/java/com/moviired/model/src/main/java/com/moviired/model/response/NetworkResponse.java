package com.moviired.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NetworkResponse implements Serializable {

    private String id;

    private String name;

    private String logo;

    private String description;

    private String carouselName;

}

