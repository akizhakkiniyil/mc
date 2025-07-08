import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity { // Extend VaadinWebSecurity

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http); // Call super.configure for Vaadin's default security setup

        http
         .authorizeHttpRequests(authorize -> authorize
                // Allow access to login/logout pages, static resources, and Vaadin internal paths
             .requestMatchers(
                    "/login/**",
                    "/oauth2/**",
                    "/error",
                    "/webjars/**",
                    "/css/**",
                    "/js/**",
                    "/images/**", // Vaadin static resources
                    "/VAADIN/**", // Vaadin internal resources
                    "/favicon.ico",
                    "/", // Root path
                    "/access-ai-api/user-info" // Allow user info endpoint for UI to check login status
                ).permitAll() // Permit all these paths without authentication
                // All other requests to /access-ai-api/** (API endpoints) require authentication
             .requestMatchers("/access-ai-api/**").authenticated()
                // All other Vaadin routes (not explicitly permitted above) require authentication
             .anyRequest().authenticated()
            )
         .oauth2Login(oauth2 -> oauth2
             .loginPage("/oauth2/authorization/azure-ad") // Redirect to Azure AD login flow
             .defaultSuccessUrl("/", true) // Redirect to root after successful login
             .userInfoEndpoint(userInfo -> userInfo
                 .oidcUserService(this.oidcUserService()) // Custom OIDC user service to process claims
                )
            )
         .logout(logout -> logout
             .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL to trigger logout
             .logoutSuccessHandler(oidcLogoutSuccessHandler()) // Custom handler for OIDC logout
             .permitAll() // Allow unauthenticated access to logout
            );
    }

    // Custom OIDC User Service to extract additional claims and potentially map to custom principal attributes
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();
        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            // Example: Map 'oid' claim (Azure AD Object ID) to employeeId
            String employeeId = oidcUser.getClaimAsString("oid");
            String employeeName = oidcUser.getFullName(); // Typically available, or oidcUser.getClaimAsString("name")

            // Return a custom OidcUser that exposes employeeId and employeeName
            // This allows us to inject @AuthenticationPrincipal OidcUser and call getEmployeeId()
            return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo()) {
                @Override
                public String getName() {
                    return employeeName; // Use the full name from OIDC user info
                }

                // Custom method to get employee ID (Azure AD Object ID)
                public String getEmployeeId() {
                    return employeeId;
                }
                // You can add more custom methods here for department, role etc.
                // public String getDepartment() { return oidcUser.getClaimAsString("department"); }
                // public String getRole() { return oidcUser.getClaimAsString("jobTitle"); }
            };
        };
    }

    // Custom Logout Success Handler for OIDC to redirect to Azure AD logout endpoint
    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            // Construct the Azure AD logout URL
            // Replace <YOUR_AZURE_AD_TENANT_ID> with your actual tenant ID
            String issuerUri = "https://login.microsoftonline.com/<YOUR_AZURE_AD_TENANT_ID>/oauth2/v2.0/logout";
            // Redirect back to the root of your application after Azure AD logout
            String redirectUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
            response.sendRedirect(issuerUri + "?post_logout_redirect_uri=" + redirectUri);
        };
    }
}
