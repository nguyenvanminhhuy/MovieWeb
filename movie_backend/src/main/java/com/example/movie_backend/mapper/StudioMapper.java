package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.StudioRequest;
import com.example.movie_backend.dto.response.StudioResponse;
import com.example.movie_backend.entity.Studio;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudioMapper {
    Studio toStudio(StudioRequest request);

    StudioResponse toStudioResponse(Studio studio);

    void updateStudio(@MappingTarget Studio studio, StudioRequest request);
}
