package com.ecomerce.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @NotBlank
    @Size(min = 4, max = 20, message = "username must be between 4 to 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "username can contain only letters, numbers and underscore")
    @Column(name = "username")
    private String username;
    @NotBlank
    @Size(min = 4, max = 100, message = "password must be between 4 to 50 characters")
    private String password;
    @Email(message = "Email formate is not correct")
    @Column(name = "email", unique = true)
    private String email;

    // Relationships

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    @Getter
    @Setter
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Cart cart;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }


}
