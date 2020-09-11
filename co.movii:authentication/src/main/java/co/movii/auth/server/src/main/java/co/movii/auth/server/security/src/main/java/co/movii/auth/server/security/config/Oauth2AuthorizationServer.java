package co.movii.auth.server.security.config;

import co.movii.auth.server.conf.CustomRedisTokenStoreConfig;
import co.movii.auth.server.properties.GlobalProperties;
import co.movii.auth.server.security.crypt.CryptoUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@Slf4j
@EnableAuthorizationServer
public class Oauth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String PSW = "password";
    private static final String READ_PROFILE = "read_profile";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final int TOKEN_MAX_LIFE = 600;
    private final RedisConnectionFactory connectionFactory;
    @Autowired
    private CryptoUtility cryptoUtility;
    @Autowired
    private CustomClientPasswordEncoder encoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private GlobalProperties globalProperties;

    public Oauth2AuthorizationServer(RedisConnectionFactory pconnectionFactory) {
        this.connectionFactory = pconnectionFactory;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // adding authenticationManager because we are supporting password grant
        endpoints.authenticationManager(authenticationManager).tokenServices(customTokenServices());
        log.info(cryptoUtility.toString());
    }

    @Bean
    DefaultTokenServices customTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(tokenEnhacer());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(false);
        tokenServices.setAccessTokenValiditySeconds(TOKEN_MAX_LIFE);
        return tokenServices;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("legacy").secret("{noop}")
                .authorizedGrantTypes(AUTHORIZATION_CODE, PSW)
                .scopes(READ_PROFILE, "read_contacts").

                // ACCESS TO PORTAL
                        and().withClient(globalProperties.getClientPortal()).secret(encoder.encode(globalProperties.getPassClientPortal()))
                .authorizedGrantTypes(AUTHORIZATION_CODE, PSW, REFRESH_TOKEN)
                .scopes(READ_PROFILE)

                // ACCESS TO SERVICE
                .and().withClient(globalProperties.getClientService()).secret(encoder.encode(globalProperties.getPasswordMobile()))
                .authorizedGrantTypes(AUTHORIZATION_CODE, PSW, REFRESH_TOKEN)
                .scopes(READ_PROFILE)

                // ACCESS TO MOBILE
                .and().withClient(globalProperties.getClientMobile()).secret(encoder.encode(globalProperties.getPasswordMobile()))
                .authorizedGrantTypes(AUTHORIZATION_CODE, PSW, REFRESH_TOKEN)
                .scopes(READ_PROFILE);
    }

    @Bean
    public TokenStore tokenStore() {
        return new CustomRedisTokenStoreConfig(connectionFactory, cryptoUtility);
    }

    @Bean
    public TokenEnhancer tokenEnhacer() {
        return new CustomTokenEnhancer();
    }

}

