package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.FranchiseRequest;
import com.example.movie_backend.dto.response.FranchiseResponse;
import com.example.movie_backend.dto.response.PageResponse;

public interface FranchiseService {
    FranchiseResponse create(FranchiseRequest request);
    PageResponse<FranchiseResponse> getAll(int page, int size);
    FranchiseResponse getById(String id);
    FranchiseResponse update(String id, FranchiseRequest request);
    void delete(String id);
}
