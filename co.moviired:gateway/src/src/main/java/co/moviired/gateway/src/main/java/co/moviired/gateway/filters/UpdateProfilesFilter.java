package co.moviired.gateway.filters;

import co.moviired.gateway.conf.ProfilesConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * This filter implementation exposes a controller to let
 * externally update the profiles in redis cache
 ***/

@Data
@Component
@EqualsAndHashCode(callSuper = false)
public final class UpdateProfilesFilter extends AbstractGatewayFilterFactory<UpdateProfilesFilter.Config> {

    @Autowired
    private ProfilesConfig profilesConfig;

    public UpdateProfilesFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            profilesConfig.loadProfiles();
            return chain.filter(exchange);
        };
    }

    public static final class Config {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String pstatus) {
            this.status = pstatus;
        }
    }

}


