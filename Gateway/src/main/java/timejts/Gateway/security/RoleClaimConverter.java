package timejts.Gateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;

import java.util.Collections;
import java.util.Map;

public class RoleClaimConverter implements
        Converter<Map<String, Object>, Map<String, Object>> {

    private final MappedJwtClaimSetConverter delegate =
            MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Override
    public Map<String, Object> convert(Map<String, Object> claims) {
        Map<String, Object> convertedClaims = this.delegate.convert(claims);

        String roles = convertedClaims.get("https://localhost:4200/roles").toString();
        String scope = convertedClaims.get("scope") + " " + roles.substring(2, roles.length() - 2);

        convertedClaims.put("scope", scope);

        return convertedClaims;
    }
}
