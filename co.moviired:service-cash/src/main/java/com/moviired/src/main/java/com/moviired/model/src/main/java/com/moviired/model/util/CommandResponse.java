package com.moviired.model.util;

import lombok.Data;

@Data
public class CommandResponse {
    private String txnid;
    private String trid;
    private String txnmode;
    private float txnstatus;
    private String type;
}

