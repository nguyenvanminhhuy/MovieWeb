package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.SubtitleRequest;
import com.example.movie_backend.dto.response.SubtitleResponse;
import com.example.movie_backend.entity.Episode;
import com.example.movie_backend.entity.Subtitle;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.SubtitleMapper;
import com.example.movie_backend.repository.EpisodeRepository;
import com.example.movie_backend.repository.SubtitleRepository;
import com.example.movie_backend.service.SubtitleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubtitleServiceImpl implements SubtitleService {
    SubtitleRepository subtitleRepository;
    EpisodeRepository episodeRepository;
    SubtitleMapper subtitleMapper;

    @Override
    public SubtitleResponse create(SubtitleRequest request) {
        Episode episode = episodeRepository.findById(request.getEpisodeId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        
        Subtitle subtitle = subtitleMapper.toSubtitle(request);
        subtitle.setEpisode(episode);
        
        return subtitleMapper.toSubtitleResponse(subtitleRepository.save(subtitle));
    }

    @Override
    public List<SubtitleResponse> getByEpisodeId(String episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        return subtitleRepository.findByEpisode(episode).stream()
                .map(subtitleMapper::toSubtitleResponse)
                .toList();
    }

    @Override
    public void delete(String id) {
        subtitleRepository.deleteById(id);
    }
}
