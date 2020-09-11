package co.moviired.auth.server.security.config;

import co.moviired.auth.server.security.CustomAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Slf4j
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthentication customAuthenticationProvider;

    @Override
    public void configure(AuthenticationManagerBuilder builder) {
        log.debug("authenticatormanagerbuilder -------->");
        builder.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.debug("configure authenticatormanagerbuilder ---->");
        http.authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .requestMatchers()
                .antMatchers("/v1/**");

    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}

