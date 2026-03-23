package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.WatchHistoryRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.WatchHistoryResponse;

import java.util.List;

public interface WatchHistoryService {
    void saveProgress(WatchHistoryRequest request);

    PageResponse<WatchHistoryResponse> getMyHistory(int page, int size);
}
