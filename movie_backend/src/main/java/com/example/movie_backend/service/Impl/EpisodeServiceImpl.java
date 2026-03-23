package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.EpisodeRequest;
import com.example.movie_backend.dto.response.EpisodeResponse;
import com.example.movie_backend.entity.Episode;
import com.example.movie_backend.entity.Movie;
import com.example.movie_backend.entity.Notification;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.EpisodeMapper;
import com.example.movie_backend.repository.EpisodeRepository;
import com.example.movie_backend.repository.FavoriteRepository;
import com.example.movie_backend.repository.MovieRepository;
import com.example.movie_backend.repository.NotificationRepository;
import com.example.movie_backend.service.EpisodeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EpisodeServiceImpl implements EpisodeService {
    EpisodeRepository episodeRepository;
    MovieRepository movieRepository;
    FavoriteRepository favoriteRepository;
    NotificationRepository notificationRepository;
    EpisodeMapper episodeMapper;

    @Override
    @Transactional
    public EpisodeResponse create(EpisodeRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));

        Episode episode = episodeMapper.toEpisode(request);
        episode.setMovie(movie);

        Episode savedEpisode = episodeRepository.save(episode);

        // Notify followers
        notifyFollowers(movie, savedEpisode);

        return episodeMapper.toEpisodeResponse(savedEpisode);
    }

    private void notifyFollowers(Movie movie, Episode episode) {
        var favorites = favoriteRepository.findByMovie(movie);
        List<Notification> notifications = favorites.stream()
                .map(f -> {
                    Notification n = new Notification();
                    n.setUser(f.getUser());
                    n.setTitle("Tập mới: " + movie.getTitle());
                    n.setMessage(
                            "Tập " + episode.getEpisodeNumber() + " (" + episode.getTitle() + ") đã được đăng tải!");
                    n.setType("NEW_EPISODE");
                    n.setTargetUrl("/movies/" + movie.getId() + "/watch?episode=" + episode.getId());
                    return n;
                })
                .toList();
        notificationRepository.saveAll(notifications);
    }

    @Override
    public List<EpisodeResponse> getByMovieId(String movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        return episodeRepository.findByMovie(movie).stream()
                .map(episodeMapper::toEpisodeResponse)
                .toList();
    }

    @Override
    public EpisodeResponse getById(String id) {
        return episodeMapper.toEpisodeResponse(episodeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)));
    }

    @Override
    public EpisodeResponse update(String id, EpisodeRequest request) {
        Episode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        episodeMapper.updateEpisode(episode, request);
        return episodeMapper.toEpisodeResponse(episodeRepository.save(episode));
    }

    @Override
    public void delete(String id) {
        episodeRepository.deleteById(id);
    }
}
