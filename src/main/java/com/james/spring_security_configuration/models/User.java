package com.james.spring_security_configuration.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.james.spring_security_configuration.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "USERS")
public class User extends AbstractAuditingEntity<User> implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @JsonIgnore
   private String password;

   private String name;

   private String email;

   @Column(name = "enabled", columnDefinition="BOOLEAN DEFAULT false")
   private boolean enabled;

   @Column(name = "activated", columnDefinition="BOOLEAN DEFAULT false")
   private boolean activated;

   private LocalDateTime lastLoginDate;

   private LocalDateTime lastPasswordResetDate;

   @Enumerated(EnumType.STRING)
   private RoleName role = RoleName.ROLE_USER;



}
