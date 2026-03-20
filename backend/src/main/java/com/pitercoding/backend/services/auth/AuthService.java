package com.pitercoding.backend.services.auth;

import com.pitercoding.backend.dto.SignupRequest;
import com.pitercoding.backend.dto.UserDTO;

public interface AuthService {

    UserDTO createUser(SignupRequest signupRequest);
}
