package com.hotel.Hotel.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data

public class LoginRequest {

    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private  String password;
}
