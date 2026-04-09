package com.pitercoding.backend.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pitercoding.backend.dto.AuthenticationRequest;
import com.pitercoding.backend.dto.SignupRequest;
import com.pitercoding.backend.dto.UserDTO;
import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.UserRole;
import com.pitercoding.backend.repository.UserRepository;
import com.pitercoding.backend.services.auth.AuthService;
import com.pitercoding.backend.services.jwt.UserService;
import com.pitercoding.backend.util.JwtUtil;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Signup returns created user when request is valid")
    void signupUser_ReturnsCreatedUser_WhenRequestIsValid() throws Exception {
        SignupRequest signupRequest = createSignupRequest();
        UserDTO userDTO = createUserDto();

        when(authService.createUser(any(SignupRequest.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDTO.getId()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$.name").value(userDTO.getName()))
                .andExpect(jsonPath("$.userRole").value(userDTO.getUserRole().name()));

        verify(authService).createUser(any(SignupRequest.class));
    }

    @Test
    @DisplayName("Signup returns not acceptable when user already exists")
    void signupUser_ReturnsNotAcceptable_WhenUserAlreadyExists() throws Exception {
        SignupRequest signupRequest = createSignupRequest();

        when(authService.createUser(any(SignupRequest.class)))
                .thenThrow(new EntityExistsException("User already present"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string("User already exists"));

        verify(authService).createUser(any(SignupRequest.class));
    }

    @Test
    @DisplayName("Signup returns bad request when unexpected error occurs")
    void signupUser_ReturnsBadRequest_WhenUnexpectedErrorOccurs() throws Exception {
        SignupRequest signupRequest = createSignupRequest();

        when(authService.createUser(any(SignupRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not created, come again later"));

        verify(authService).createUser(any(SignupRequest.class));
    }

    @Test
    @DisplayName("Login returns jwt response when credentials are valid")
    void createAuthenticationResponse_ReturnsJwtResponse_WhenCredentialsAreValid() throws Exception {
        AuthenticationRequest authenticationRequest = createAuthenticationRequest();
        User user = createUser(1L, "rachacuca@test.com", "123456", "Racha Cuca", UserRole.CUSTOMER);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(authenticationRequest.getEmail()))
                .thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("jwt-token"))
                .andExpect(jsonPath("$.userRole").value(UserRole.CUSTOMER.name()))
                .andExpect(jsonPath("$.userId").value(1L));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).userDetailsService();
        verify(jwtUtil).generateToken(user);
    }

    @Test
    @DisplayName("Login throws bad credentials exception when credentials are invalid")
    void createAuthenticationResponse_ThrowsBadCredentialsException_WhenCredentialsAreInvalid() throws Exception {
        AuthenticationRequest authenticationRequest = createAuthenticationRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest))))
                .hasCauseInstanceOf(BadCredentialsException.class)
                .rootCause()
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Incorrect username or password.");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).userDetailsService();
    }

    @Test
    @DisplayName("Login returns unauthorized when user details is not a user instance")
    void createAuthenticationResponse_ReturnsUnauthorized_WhenUserDetailsIsNotUserInstance() throws Exception {
        AuthenticationRequest authenticationRequest = createAuthenticationRequest();
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(authenticationRequest.getEmail()))
                .thenReturn(userDetails);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).userDetailsService();
        verify(jwtUtil, never()).generateToken(any(User.class));
    }

    private SignupRequest createSignupRequest() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("rachacuca@test.com");
        signupRequest.setPassword("123456");
        signupRequest.setName("Racha Cuca");
        return signupRequest;
    }

    private UserDTO createUserDto() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("rachacuca@test.com");
        userDTO.setName("Racha Cuca");
        userDTO.setUserRole(UserRole.CUSTOMER);
        return userDTO;
    }

    private AuthenticationRequest createAuthenticationRequest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("rachacuca@test.com");
        authenticationRequest.setPassword("123456");
        return authenticationRequest;
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