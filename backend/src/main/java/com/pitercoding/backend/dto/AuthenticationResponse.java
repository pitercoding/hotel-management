package com.pitercoding.backend.dto;

import com.pitercoding.backend.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;
    private Long userId;
    private UserRole userRole;
}
