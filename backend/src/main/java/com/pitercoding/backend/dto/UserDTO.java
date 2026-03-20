package com.pitercoding.backend.dto;

import com.pitercoding.backend.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private UserRole userRole;
}
