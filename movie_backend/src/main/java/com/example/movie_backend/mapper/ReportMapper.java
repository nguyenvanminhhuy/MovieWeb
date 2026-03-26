package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.ReportRequest;
import com.example.movie_backend.dto.response.ReportResponse;
import com.example.movie_backend.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "episode", ignore = true)
    Report toReport(ReportRequest request);

    ReportResponse toReportResponse(Report report);
}
