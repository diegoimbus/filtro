package co.moviired.supportp2pvalidatortransaction.provider.supportsms;

import co.moviired.supportp2pvalidatortransaction.common.provider.IProviderProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static co.moviired.supportp2pvalidatortransaction.util.Constants.PREFIX_SUPPORT_SMS;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = PREFIX_SUPPORT_SMS)
public class SupportSMSProperties extends IProviderProperties {

    private String cellPrefix;
    private String successSendMoneySMS;
    private String errorSendMoneySMS;
    private String genericMessage;
}

