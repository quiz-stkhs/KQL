package com.example.kql;

public class Member {
    private String name;
    private String email;
    private String role;

    public Member() {
    }

    public Member(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getRole() { return role; }
}
