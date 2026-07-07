package com.example.demo.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GoogleOAuth2AuthoritiesMapper implements GrantedAuthoritiesMapper {

    private final Set<String> adminEmails;

    public GoogleOAuth2AuthoritiesMapper(Set<String> adminEmails) {
        this.adminEmails = normalize(adminEmails);
    }

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>(authorities);
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        String email = extractEmail(authorities);
        if (email != null && adminEmails.contains(email.toLowerCase(Locale.ROOT))) {
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return mappedAuthorities;
    }

    private static Set<String> normalize(Set<String> emails) {
        Set<String> normalized = new HashSet<>();
        for (String email : emails) {
            String value = email.trim();
            if (!value.isEmpty()) {
                normalized.add(value.toLowerCase(Locale.ROOT));
            }
        }
        return normalized;
    }

    private static String extractEmail(Collection<? extends GrantedAuthority> authorities) {
        for (GrantedAuthority authority : authorities) {
            Map<String, Object> attributes = attributes(authority);
            Object email = attributes.get("email");
            if (email instanceof String value && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static Map<String, Object> attributes(GrantedAuthority authority) {
        if (authority instanceof OidcUserAuthority oidcUserAuthority) {
            return oidcUserAuthority.getAttributes();
        }
        if (authority instanceof OAuth2UserAuthority oAuth2UserAuthority) {
            return oAuth2UserAuthority.getAttributes();
        }
        return Map.of();
    }
}
