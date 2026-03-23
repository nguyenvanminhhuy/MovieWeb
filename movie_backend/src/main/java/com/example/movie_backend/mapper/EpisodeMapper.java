package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.EpisodeRequest;
import com.example.movie_backend.dto.response.EpisodeResponse;
import com.example.movie_backend.entity.Episode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EpisodeMapper {
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "videoSources", ignore = true)
    @Mapping(target = "subtitles", ignore = true)
    Episode toEpisode(EpisodeRequest request);

    EpisodeResponse toEpisodeResponse(Episode episode);

    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "videoSources", ignore = true)
    @Mapping(target = "subtitles", ignore = true)
    void updateEpisode(@MappingTarget Episode episode, EpisodeRequest request);
}
