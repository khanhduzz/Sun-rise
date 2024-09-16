package com.fjb.sunrise.dtos.responses;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String avatarImage; // Base64 encoded image data
}
