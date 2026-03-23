package com.example.movie_backend.service;

import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.entity.AuditLog;

public interface AuditLogService {
    void log(String action, String details);
    PageResponse<AuditLog> getAll(int page, int size);
}
