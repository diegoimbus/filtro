package co.moviired.moneytransfer.client.blacklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackListResponse implements Serializable {

    private String code;
    private String message;
    private String errorType;
    private String errorMessage;
    private Item item;


}

