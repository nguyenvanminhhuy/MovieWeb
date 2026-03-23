package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.SubtitleRequest;
import com.example.movie_backend.dto.response.SubtitleResponse;
import com.example.movie_backend.entity.Subtitle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface SubtitleMapper {
    @Mapping(target = "episode", ignore = true)
    Subtitle toSubtitle(SubtitleRequest request);

    SubtitleResponse toSubtitleResponse(Subtitle subtitle);

    @Mapping(target = "episode", ignore = true)
    void updateSubtitle(@MappingTarget Subtitle subtitle, SubtitleRequest request);
}
