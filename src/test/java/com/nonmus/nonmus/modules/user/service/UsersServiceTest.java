package com.nonmus.nonmus.modules.user.service;

import com.nonmus.nonmus.modules.user.dto.request.OAuthUserCreateRequest;
import com.nonmus.nonmus.modules.user.dto.request.UserCreateRequest;
import com.nonmus.nonmus.modules.user.entity.Providers;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsersService usersService;

    @Captor
    private ArgumentCaptor<Users> userCaptor;

    @Test
    void createUser_shouldMapEncodeAndSave() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("Test");
        request.setEmail("test@test.com");
        request.setPassword("rawPassword");

        Users mappedUser = new Users();
        when(modelMapper.map(request, Users.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(usersRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Users result = usersService.createUser(request);

        assertSame(mappedUser, result);
        assertEquals("encodedPassword", mappedUser.getPassword());
        assertEquals(Provider.LOCAL, mappedUser.getProfilePictureProvider());
        assertFalse(mappedUser.getProviders().isEmpty());
        assertEquals(Provider.LOCAL, mappedUser.getProviders().get(0).getProvider());
        verify(usersRepository).save(mappedUser);
    }

    @Test
    void createOAuthUser_shouldSetFieldsAndSave() {
        OAuthUserCreateRequest request = new OAuthUserCreateRequest();
        request.setName("OAuth User");
        request.setEmail("oauth@test.com");
        request.setExternalProfilePictureUrl("https://example.com/avatar.jpg");
        request.setProvider(Provider.GOOGLE);
        request.setEmailVerified(true);

        when(usersRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Users result = usersService.createOAuthUser(request);

        assertEquals("OAuth User", result.getName());
        assertEquals("oauth@test.com", result.getEmail());
        assertEquals("https://example.com/avatar.jpg", result.getProfilePicture());
        assertTrue(result.getEmailVerified());
        assertEquals(Provider.GOOGLE, result.getProfilePictureProvider());
        assertFalse(result.getProviders().isEmpty());
        assertEquals(Provider.GOOGLE, result.getProviders().get(0).getProvider());
        verify(usersRepository).save(result);
    }

    @Test
    void getUsersByEmail_shouldDelegateToRepository() {
        Users user = new Users();
        user.setEmail("test@test.com");

        when(usersRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Optional<Users> result = usersService.getUsersByEmail("test@test.com");

        assertTrue(result.isPresent());
        assertSame(user, result.get());
    }

    @Test
    void existsByEmail_shouldDelegateToRepository() {
        when(usersRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertTrue(usersService.existsByEmail("test@test.com"));
    }

    @Test
    void addProviderToUserAndSave_shouldAddProviderAndSave() {
        Users user = new Users();
        when(usersRepository.save(user)).thenReturn(user);

        usersService.addProviderToUserAndSave(user, Provider.GOOGLE);

        assertEquals(1, user.getProviders().size());
        assertEquals(Provider.GOOGLE, user.getProviders().get(0).getProvider());
        verify(usersRepository).save(user);
    }
}