package com.smarttender.auth.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class RegisterRequest {
    @NotBlank @Size(max = 50)
    private String firstName;
    @NotBlank @Size(max = 50)
    private String lastName;
    @NotBlank @Email @Size(max = 100)
    private String email;
    @NotBlank @Size(min = 8, max = 100)
    private String password;
}
