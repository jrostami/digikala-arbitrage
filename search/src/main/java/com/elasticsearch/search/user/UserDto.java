package com.elasticsearch.search.user;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private String email;
    String name;
    private String password;
    private Set<String> roles;

    // Getters and Setters
    public UserDto(){

    }
    public UserDto(User user){
        this.name = user.name;
        this.email = user.getUsername();
        this.roles = user.getRoles();
    }
}
