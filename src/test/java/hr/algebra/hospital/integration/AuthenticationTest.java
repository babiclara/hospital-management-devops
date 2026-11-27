package hr.algebra.hospital.integration;

import hr.algebra.hospital.repository.AppUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationTest extends IntegrationTestBase {

    @Autowired
    MockMvc mvc;

    @Autowired
    AppUserRepository users;

    @Test
    @DisplayName("the login page is reachable without signing in")
    void loginPageIsPublic() throws Exception {
        mvc.perform(get("/login"))
           .andExpect(status().isOk())
           .andExpect(content().string(org.hamcrest.Matchers.containsString("Sign in")));
    }

    @Test
    @DisplayName("a protected page redirects an anonymous visitor to the login page")
    void protectedPageRedirects() throws Exception {
        mvc.perform(get("/"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("correct credentials sign the user in")
    void validCredentialsSignIn() throws Exception {
        mvc.perform(formLogin("/login").user("receptionist").password("recep123"))
           .andExpect(authenticated().withUsername("receptionist"))
           .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("a wrong password does not sign the user in")
    void wrongPasswordIsRejected() throws Exception {
        mvc.perform(formLogin("/login").user("receptionist").password("not-the-password"))
           .andExpect(unauthenticated())
           .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @DisplayName("an unknown username does not sign the user in")
    void unknownUserIsRejected() throws Exception {
        mvc.perform(formLogin("/login").user("nobody").password("whatever"))
           .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("signing out ends the session")
    void signOutEndsSession() throws Exception {
        mvc.perform(logout())
           .andExpect(unauthenticated())
           .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    @DisplayName("every stored password is a BCrypt hash, never plain text")
    void passwordsAreHashed() {
        assertThat(users.findAll())
                .isNotEmpty()
                .allSatisfy(user -> {
                    assertThat(user.getPasswordHash()).startsWith("$2");
                    assertThat(user.getPasswordHash()).hasSizeGreaterThanOrEqualTo(59);
                    assertThat(user.getPasswordHash()).doesNotContain(user.getUsername());
                });
    }
}
