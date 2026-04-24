package com.serviya.msservicerequest.security;

public record JwtPrincipal(Long userId, String email, String token) {
}
