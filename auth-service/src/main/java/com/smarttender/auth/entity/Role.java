package com.smarttender.auth.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "roles", schema = "smart_tender_auth")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RoleName name;
    public enum RoleName { ROLE_USER, ROLE_ADMIN }
}
