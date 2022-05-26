package com.github.klefstad_teaching.cs122b.idm.model.request;

public class RegisterAndLoginRequest {
    private String email;
    private char[] password;

    public String getEmail() {
        return email;
    }

    public RegisterAndLoginRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public char[] getPassword() {
        return password;
    }

    public RegisterAndLoginRequest setPassword(char[] password) {
        this.password = password;
        return this;
    }
}
