package com.pitercoding.backend.services.auth;

import com.pitercoding.backend.dto.SignupRequest;
import com.pitercoding.backend.dto.UserDTO;
import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.UserRole;
import com.pitercoding.backend.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Test
    @DisplayName("Create admin account creates admin when admin does not exist")
    void createAnAdminAccount_CreatesAdmin_WhenAdminDoesNotExist() {
        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.empty());

        authServiceImpl.createAnAdminAccount();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo("admin@test.com");
        assertThat(savedUser.getName()).isEqualTo("Admin");
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.ADMIN);
        assertThat(savedUser.getPassword()).isNotNull();
        assertThat(savedUser.getPassword()).isNotEqualTo("admin");
        assertThat(new BCryptPasswordEncoder().matches("admin", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Create admin account does not create admin when admin already exists")
    void createAnAdminAccount_DoesNotCreateAdmin_WhenAdminAlreadyExists() {
        User adminUser = createUser(1L, "admin@test.com", "admin", "Admin", UserRole.ADMIN);

        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.of(adminUser));

        authServiceImpl.createAnAdminAccount();

        verify(userRepository).findByUserRole(UserRole.ADMIN);
        verify(userRepository, never()).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Create user returns created user when email does not exist")
    void createUser_ReturnsCreatedUser_WhenEmailDoesNotExist() {
        SignupRequest signupRequest = createSignupRequest("customer@test.com", "123456", "Customer");
        User createdUser = createUser(1L, "customer@test.com", "encoded-password", "Customer", UserRole.CUSTOMER);

        when(userRepository.findFirstByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(createdUser);

        UserDTO newUser = authServiceImpl.createUser(signupRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(newUser.getId()).isEqualTo(1L);
        assertThat(newUser.getName()).isEqualTo("Customer");
        assertThat(newUser.getEmail()).isEqualTo("customer@test.com");
        assertThat(newUser.getUserRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(savedUser.getEmail()).isEqualTo(signupRequest.getEmail());
        assertThat(savedUser.getName()).isEqualTo(signupRequest.getName());
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(savedUser.getPassword()).isNotEqualTo(signupRequest.getPassword());
        assertThat(new BCryptPasswordEncoder().matches(signupRequest.getPassword(), savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Create user throws exception when email already exists")
    void createUser_ThrowsEntityExistsException_WhenEmailAlreadyExists() {
        SignupRequest signupRequest = createSignupRequest("customer@test.com", "123456", "Customer");
        User existingUser = createUser(1L, "customer@test.com", "encoded-password", "Customer", UserRole.CUSTOMER);

        when(userRepository.findFirstByEmail(signupRequest.getEmail()))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authServiceImpl.createUser(signupRequest))
                .isInstanceOf(EntityExistsException.class)
                .hasMessage("User already present with email " + signupRequest.getEmail());

        verify(userRepository).findFirstByEmail(signupRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    private SignupRequest createSignupRequest(
            String email,
            String password,
            String name) {
        SignupRequest signupRequest = new SignupRequest();

        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        signupRequest.setName(name);

        return signupRequest;
    }

    private User createUser(
            Long id,
            String email,
            String password,
            String name,
            UserRole userRole) {

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setUserRole(userRole);
        return user;
    }
}
