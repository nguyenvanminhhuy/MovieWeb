package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.CommentRequest;
import com.example.movie_backend.dto.response.CommentResponse;
import com.example.movie_backend.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "replies", ignore = true)
    Comment toComment(CommentRequest request);

    CommentResponse toCommentResponse(Comment comment);
}
