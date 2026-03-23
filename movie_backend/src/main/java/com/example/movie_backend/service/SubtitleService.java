package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.SubtitleRequest;
import com.example.movie_backend.dto.response.SubtitleResponse;

import java.util.List;

public interface SubtitleService {
    SubtitleResponse create(SubtitleRequest request);
    List<SubtitleResponse> getByEpisodeId(String episodeId);
    void delete(String id);
}
