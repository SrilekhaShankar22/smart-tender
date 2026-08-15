package com.smarttender.auth.dto.response;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean emailVerified;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
