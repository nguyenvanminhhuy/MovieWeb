package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.VideoSourceRequest;
import com.example.movie_backend.dto.response.VideoSourceResponse;
import com.example.movie_backend.entity.Episode;
import com.example.movie_backend.entity.VideoSource;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.VideoSourceMapper;
import com.example.movie_backend.repository.EpisodeRepository;
import com.example.movie_backend.repository.VideoSourceRepository;
import com.example.movie_backend.service.VideoSourceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoSourceServiceImpl implements VideoSourceService {
    VideoSourceRepository videoSourceRepository;
    EpisodeRepository episodeRepository;
    VideoSourceMapper videoSourceMapper;

    @Override
    public VideoSourceResponse create(VideoSourceRequest request) {
        Episode episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        
        VideoSource videoSource = videoSourceMapper.toVideoSource(request);
        videoSource.setEpisode(episode);
        
        return videoSourceMapper.toVideoSourceResponse(videoSourceRepository.save(videoSource));
    }

    @Override
    public List<VideoSourceResponse> getByEpisodeId(String episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        return videoSourceRepository.findByEpisode(episode).stream()
                .map(videoSourceMapper::toVideoSourceResponse)
                .toList();
    }

    @Override
    public void delete(String id) {
        videoSourceRepository.deleteById(id);
    }
}
