package com.nonmus.nonmus.filter;

import com.nonmus.nonmus.modules.common.util.JwtUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import com.nonmus.nonmus.security.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsersService usersService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChain_whenCookieIsMissing() throws Exception {

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueFilterChain_whenCookieNameIsWrong() throws Exception {
        request.setCookies(new Cookie("improper_access_token", "token"));

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(any(), any());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotAuthenticate_whenTokenIsInvalid() throws Exception {
        request.setCookies(new Cookie("access_token", "token"));

        when(jwtUtil.isTokenValid("token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotAuthenticate_whenUserDoesNotExist() throws Exception {

        request.setCookies(new Cookie("access_token", "token"));

        when(jwtUtil.isTokenValid("token")).thenReturn(true);
        when(jwtUtil.getEmailFromToken("token")).thenReturn("abc@test.com");
        when(usersService.getUsersByEmail("abc@test.com"))
                .thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldAuthenticate_whenTokenIsValidAndUserExists() throws Exception {

        Users user = new Users();
        user.setEmail("abc@test.com");

        request.setCookies(new Cookie("access_token", "token"));

        when(jwtUtil.isTokenValid("token")).thenReturn(true);
        when(jwtUtil.getEmailFromToken("token")).thenReturn("abc@test.com");
        when(usersService.getUsersByEmail("abc@test.com"))
                .thenReturn(Optional.of(user));

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);

        AuthenticatedUser principal =
                (AuthenticatedUser) authentication.getPrincipal();

        assertEquals("abc@test.com", principal.getEmail());

        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldNotReplaceExistingAuthentication() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("existing", null));

        request.setCookies(new Cookie("access_token", "token"));

        filter.doFilter(request, response, filterChain);

        assertEquals(
                "existing",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verifyNoInteractions(jwtUtil);
    }
}