package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.VideoSourceRequest;
import com.example.movie_backend.dto.response.VideoSourceResponse;

import java.util.List;

public interface VideoSourceService {
    VideoSourceResponse create(VideoSourceRequest request);
    List<VideoSourceResponse> getByEpisodeId(String episodeId);
    void delete(String id);
}
