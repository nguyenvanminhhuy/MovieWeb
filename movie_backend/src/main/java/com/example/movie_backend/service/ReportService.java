package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.ReportRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.ReportResponse;

public interface ReportService {
    ReportResponse create(ReportRequest request);
    PageResponse<ReportResponse> getAll(int page, int size);
    void resolve(String id);
}
