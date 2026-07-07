package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleOAuth2AuthoritiesMapperTest {

    @Test
    void mapsEveryGoogleUserToUserRole() {
        GoogleOAuth2AuthoritiesMapper mapper = new GoogleOAuth2AuthoritiesMapper(Set.of("admin@example.com"));

        List<String> authorities = mapper.mapAuthorities(List.of(
                        new OAuth2UserAuthority(Map.of("email", "user@example.com"))
                )).stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities).contains("ROLE_USER");
        assertThat(authorities).doesNotContain("ROLE_ADMIN");
    }

    @Test
    void mapsConfiguredAdminEmailToAdminRoleIgnoringCase() {
        GoogleOAuth2AuthoritiesMapper mapper = new GoogleOAuth2AuthoritiesMapper(Set.of("admin@example.com"));

        List<String> authorities = mapper.mapAuthorities(List.of(
                        new OAuth2UserAuthority(Map.of("email", "Admin@Example.com"))
                )).stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities).contains("ROLE_USER", "ROLE_ADMIN");
    }
}
