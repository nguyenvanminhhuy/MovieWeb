package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.GenreRequest;
import com.example.movie_backend.dto.response.GenreResponse;
import com.example.movie_backend.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    Genre toGenre(GenreRequest request);

    GenreResponse toGenreResponse(Genre genre);

    void updateGenre(@MappingTarget Genre genre, GenreRequest request);
}
