package com.event.event_management.dto;

import com.event.event_management.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Role role;

}