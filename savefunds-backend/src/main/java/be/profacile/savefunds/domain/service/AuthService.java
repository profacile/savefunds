package be.profacile.savefunds.domain.service;

import be.profacile.savefunds.api.dto.request.LoginRequest;
import be.profacile.savefunds.api.dto.request.RegisterRequest;
import be.profacile.savefunds.api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}