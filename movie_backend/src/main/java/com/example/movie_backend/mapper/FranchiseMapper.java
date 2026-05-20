package com.example.movie_backend.mapper;

import com.example.movie_backend.dto.request.FranchiseRequest;
import com.example.movie_backend.dto.response.FranchiseResponse;
import com.example.movie_backend.entity.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {
    Franchise toFranchise(FranchiseRequest request);

    FranchiseResponse toFranchiseResponse(Franchise franchise);

    void updateFranchise(@MappingTarget Franchise franchise, FranchiseRequest request);
}
