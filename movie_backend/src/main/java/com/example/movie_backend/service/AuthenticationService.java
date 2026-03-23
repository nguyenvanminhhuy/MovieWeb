package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.AuthenticationRequest;
import com.example.movie_backend.dto.request.IntrospectRequest;
import com.example.movie_backend.dto.response.AuthenticationResponse;
import com.example.movie_backend.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
}
