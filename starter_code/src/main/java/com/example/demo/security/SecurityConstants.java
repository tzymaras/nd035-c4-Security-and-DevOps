package com.example.demo.security;

public interface SecurityConstants {
    long EXPIRATION_TIME = 864_000_000;
    String SECRET = "secret";
    String HEADER_STRING = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    String SIGNUP_URL = "/api/user/create";
}
