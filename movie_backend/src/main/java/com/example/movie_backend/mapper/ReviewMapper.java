package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.ReviewRequest;
import com.example.movie_backend.dto.response.ReviewResponse;
import com.example.movie_backend.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    Review toReview(ReviewRequest request);

    @Mapping(source = "movie.title", target = "movieTitle")
    ReviewResponse toReviewResponse(Review review);
}
