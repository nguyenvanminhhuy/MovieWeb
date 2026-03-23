package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.WatchHistoryRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.WatchHistoryResponse;
import com.example.movie_backend.entity.Episode;
import com.example.movie_backend.entity.User;
import com.example.movie_backend.entity.WatchHistory;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.EpisodeMapper;
import com.example.movie_backend.mapper.MovieMapper;
import com.example.movie_backend.repository.EpisodeRepository;
import com.example.movie_backend.repository.UserRepository;
import com.example.movie_backend.repository.WatchHistoryRepository;
import com.example.movie_backend.service.WatchHistoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WatchHistoryServiceImpl implements WatchHistoryService {
        WatchHistoryRepository watchHistoryRepository;
        UserRepository userRepository;
        EpisodeRepository episodeRepository;
        MovieMapper movieMapper;
        EpisodeMapper episodeMapper;

        @Override
        public void saveProgress(WatchHistoryRequest request) {
                User user = getCurrentUser();
                Episode episode = episodeRepository.findById(request.getEpisodeId())
                                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

                WatchHistory history = watchHistoryRepository.findByUserAndEpisode(user, episode)
                                .orElse(new WatchHistory());

                history.setUser(user);
                history.setEpisode(episode);
                history.setProgress(request.getProgress());

                watchHistoryRepository.save(history);
        }

        @Override
        public PageResponse<WatchHistoryResponse> getMyHistory(int page, int size) {
                User user = getCurrentUser();
                Sort sort = Sort.by("updatedAt").descending();
                Pageable pageable = PageRequest.of(page - 1, size, sort);
                var pageData = watchHistoryRepository.findByUser(user, pageable);

                return PageResponse.<WatchHistoryResponse>builder()
                                .currentPage(page)
                                .pageSize(size)
                                .totalPages(pageData.getTotalPages())
                                .totalElements(pageData.getTotalElements())
                                .data(pageData.getContent().stream()
                                                .map(history -> WatchHistoryResponse.builder()
                                                                .id(history.getId())
                                                                .movie(movieMapper.toMovieResponse(
                                                                                history.getEpisode().getMovie()))
                                                                .episode(episodeMapper.toEpisodeResponse(
                                                                                history.getEpisode()))
                                                                .progress(history.getProgress())
                                                                .updatedAt(history.getUpdatedAt())
                                                                .build())
                                                .toList())
                                .build();
        }

        private User getCurrentUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }
}
