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
public class BlackListRequest implements Serializable {
    private String documentNumber;
    private String userName;
}

