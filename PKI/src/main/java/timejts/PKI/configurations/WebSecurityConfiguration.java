package timejts.PKI.configurations;

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
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .anyRequest().authenticated().and()
                .x509()
                .authenticationUserDetailsService(new X509AuthenticatedUserDetailsService());
        http.headers()
                .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy",
                        "default-src 'self'"))
                .addHeaderWriter(new StaticHeadersWriter("X-WebKit-CSP", "default-src 'self'"));
    }

    protected class X509AuthenticatedUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

        @Override
        public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token)
                throws UsernameNotFoundException {
            X509Certificate certificate = (X509Certificate) token.getCredentials();
            User user;
            System.out.println(certificate.getSerialNumber().toString());
            if (certificate.getSerialNumber().toString().equals("252263157")) {
                GrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_CLIENT");
                List<GrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(authority1);
                user = new User(certificate.getSubjectX500Principal().getName(), "", authorityList);
            }
            else if (certificate.getSerialNumber().toString().equals("801900629")) {
                GrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_CSR");
                List<GrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(authority1);
                user = new User(certificate.getSubjectX500Principal().getName(), "", authorityList);
            }
            else {
                GrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_VALIDATOR");
                List<GrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(authority1);
                user = new User(certificate.getSubjectX500Principal().getName(), "", authorityList);
            }

            return user;
        }
    }
}
