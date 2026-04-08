package com.pitercoding.backend.services.jwt;

import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.UserRole;
import com.pitercoding.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("User details service returns user details when user exists")
    void userDetailsService_ReturnsUserDetails_WhenUserExists() {
        User user = createUser();

        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = userServiceImpl.userDetailsService();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        verify(userRepository).findFirstByEmail(user.getEmail());
    }

    @Test
    @DisplayName("User details service throws exception when user does not exist")
    void userDetailsService_ThrowsUsernameNotFoundException_WhenUserDoesNotExist() {
        String email = "email@test.com";

        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = userServiceImpl.userDetailsService();

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findFirstByEmail(email);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("123456");
        user.setName("Racha Cuca");
        user.setUserRole(UserRole.CUSTOMER);
        return user;
    }
}