package com.pitercoding.backend.repository;

import com.pitercoding.backend.entity.User;
import com.pitercoding.backend.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find first by email returns user when successful")
    void findFirstByEmail_ReturnsUser_WhenSuccessful() {
        User user = createUserAdmin();
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findFirstByEmail(user.getEmail());

        assertThat(userOptional.isPresent()).isTrue();
        assertThat(userOptional.get().getUserRole()).isEqualTo(user.getUserRole());
        assertThat(userOptional.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Find first by email returns empty when email does not exist")
    void findFirstByEmail_ReturnsEmpty_WhenEmailDoesNotExist() {

        User user = createUserAdmin();
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findFirstByEmail("missing@test.com");

        assertThat(userOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by user role returns admin when successful")
    void findByUserRole_ReturnsAdmin_WhenSuccessful() {

        User user = createUserAdmin();
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findByUserRole(UserRole.ADMIN);

        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(userOptional.get().getUserRole()).isEqualTo(user.getUserRole());
    }

    @Test
    @DisplayName("Find by user role returns customer when successful")
    void findByUserRole_ReturnsCustomer_WhenSuccessful() {

        User user = createUserCustomer();
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findByUserRole(UserRole.CUSTOMER);

        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(userOptional.get().getUserRole()).isEqualTo(user.getUserRole());
    }

    @Test
    @DisplayName("Find by user role returns empty when role does not exist")
    void findByUserRole_ReturnsEmpty_WhenRoleDoesNotExist() {

        User user = createUserCustomer();
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findByUserRole(UserRole.ADMIN);

        assertThat(userOptional).isEmpty();
    }

    private User createUserAdmin() {
        User user = new User();
        user.setEmail("admin@test.com");
        user.setPassword("admin");
        user.setName("Admin");
        user.setUserRole(UserRole.ADMIN);
        return user;
    }

    private User createUserCustomer() {
        User user = new User();
        user.setEmail("customer@test.com");
        user.setPassword("customer");
        user.setName("Customer");
        user.setUserRole(UserRole.CUSTOMER);
        return user;
    }
}
