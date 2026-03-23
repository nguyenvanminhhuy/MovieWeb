package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.VideoSourceRequest;
import com.example.movie_backend.dto.response.VideoSourceResponse;
import com.example.movie_backend.entity.VideoSource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VideoSourceMapper {
    @Mapping(target = "episode", ignore = true)
    VideoSource toVideoSource(VideoSourceRequest request);

    VideoSourceResponse toVideoSourceResponse(VideoSource videoSource);

    @Mapping(target = "episode", ignore = true)
    void updateVideoSource(@MappingTarget VideoSource videoSource, VideoSourceRequest request);
}
