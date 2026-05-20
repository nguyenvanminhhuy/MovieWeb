package com.example.movie_backend.controller;

import com.example.movie_backend.dto.request.AuthenticationRequest;
import com.example.movie_backend.dto.request.IntrospectRequest;
import com.example.movie_backend.dto.request.LogoutRequest;
import com.example.movie_backend.dto.request.RefreshRequest;
import com.example.movie_backend.dto.response.ApiResponse;
import com.example.movie_backend.dto.response.AuthenticationResponse;
import com.example.movie_backend.dto.response.IntrospectResponse;
import com.example.movie_backend.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication Controller", description = "Xác thực người dùng và quản lý Token")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @Operation(summary = "Đăng nhập hệ thống", description = "Trả về Token JWT nếu thông tin đăng nhập chính xác")
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @Operation(summary = "Kiểm tra tính hợp lệ của Token", description = "Kiểm tra xem Token JWT còn hiệu lực hay không")
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @Operation(summary = "Đăng xuất tài khoản", description = "Vô hiệu hóa Token hiện tại")
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request)
            throws JOSEException, ParseException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @Operation(summary = "Làm mới Token", description = "Sử dụng Refresh Token để lấy Access Token mới")
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody @Valid RefreshRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.refresh(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}