package timejts.SIEMCentre.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.client.RestTemplate;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    RestTemplate restTemplate;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated().and()
                .x509()
                .authenticationUserDetailsService(new X509AuthenticatedUserDetailsService());
    }

    protected class X509AuthenticatedUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

        @Override
        public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
                throws UsernameNotFoundException {
            X509Certificate certificate = (X509Certificate) token.getCredentials();

            Boolean valid;
            try {
                valid = (Boolean) restTemplate
                        .postForEntity("https://localhost:8443/api/certificates/validate", certificate
                                .getEncoded(), Object.class)
                        .getBody();
            } catch (Exception e) {
                throw new UsernameNotFoundException(e.getMessage());
            }

            User user = null;
            if (valid) {
                GrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_AGENT");
                List<GrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(authority1);
                user = new User(certificate.getSubjectX500Principal().getName(), "", authorityList);
            }

            return user;
        }
    }
}
