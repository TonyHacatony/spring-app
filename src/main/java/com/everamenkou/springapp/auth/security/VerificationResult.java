package com.everamenkou.springapp.auth.security;

import io.jsonwebtoken.Claims;

public record VerificationResult(Claims claims, String token) {
}
