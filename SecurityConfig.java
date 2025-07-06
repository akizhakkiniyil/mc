package com.hackathon.accessguardian.mcp.client.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // All paths are protected by default
        super.configure(http);

        // This is the standard login page URL provided by Spring Security's OAuth2 client support.
        // The 'azure' part matches the default registration ID created by the Azure starter.
        setOAuth2LoginPage(http, "/oauth2/authorization/azure");
    }
}