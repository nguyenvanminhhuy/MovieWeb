package com.example.movie_backend.service;

import com.example.movie_backend.dto.request.EpisodeRequest;
import com.example.movie_backend.dto.response.EpisodeResponse;

import java.util.List;

public interface EpisodeService {
    EpisodeResponse create(EpisodeRequest request);
    List<EpisodeResponse> getByMovieId(String movieId);
    EpisodeResponse getById(String id);
    EpisodeResponse update(String id, EpisodeRequest request);
    void delete(String id);
}
