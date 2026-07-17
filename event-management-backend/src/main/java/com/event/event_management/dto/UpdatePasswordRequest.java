package com.event.event_management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
}
