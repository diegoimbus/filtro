package co.movii.auth.server.security.config;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

public final class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation((Map<String, Object>) authentication.getUserAuthentication().getDetails());
        return accessToken;
    }

}

