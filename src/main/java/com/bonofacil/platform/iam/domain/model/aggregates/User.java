package com.bonofacil.platform.iam.domain.model.aggregates;

import com.bonofacil.platform.iam.domain.model.entities.Role;
import com.bonofacil.platform.profiles.domain.model.aggregates.Profile;
import com.bonofacil.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User extends AuditableAbstractAggregateRoot<User> {

    @Getter
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @Getter
    @NotBlank
    @Size(max = 120)
    private String password;

    @Getter
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Getter
    @OneToOne(fetch = FetchType.LAZY) // Sin cascade ALL inicialmente
    @JoinColumn(name = "profile_id", unique = true)
    private Profile profile;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, List<Role> roles) {
        this(username, password);
        addRoles(roles);
    }

    public User(String username, String password, Profile profile) {
        this(username, password);
        this.profile = profile;
    }

    public User(String username, String password, List<Role> roles, Profile profile) {
        this(username, password, roles);
        this.profile = profile;
    }

    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public User addRoles(List<Role> roles) {
        var validatedRoles = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoles);
        return this;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}