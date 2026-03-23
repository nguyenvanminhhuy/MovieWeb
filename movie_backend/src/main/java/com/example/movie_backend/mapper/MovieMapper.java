package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.MovieRequest;
import com.example.movie_backend.dto.response.MovieResponse;
import com.example.movie_backend.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "studio", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "episodes", ignore = true)
    Movie toMovie(MovieRequest request);

    MovieResponse toMovieResponse(Movie movie);

    @Mapping(target = "studio", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "episodes", ignore = true)
    void updateMovie(@MappingTarget Movie movie, MovieRequest request);
}
