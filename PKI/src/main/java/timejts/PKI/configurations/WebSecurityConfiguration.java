package timejts.PKI.configurations;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${certificates.serialNumber.client}")
    private String clientCertificateSerNum;

    @Value("${certificates.serialNumber.csr-creator}")
    private String csrCreatorSerNum;

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
            String authority = "ROLE_VALIDATOR";
            String serialNumber = certificate.getSerialNumber().toString();

            if (serialNumber.equals(clientCertificateSerNum)) {
                authority = "ROLE_CLIENT";
            } else if (serialNumber.equals(csrCreatorSerNum)) {
                authority = "ROLE_CSR";
            }

            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);
            List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>() {{
                add(grantedAuthority);
            }};

            return new User(certificate.getSubjectX500Principal().getName(), "", authorityList);
        }
    }
}
