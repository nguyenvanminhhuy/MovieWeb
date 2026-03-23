package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.FranchiseRequest;
import com.example.movie_backend.dto.response.FranchiseResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.entity.Franchise;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.FranchiseMapper;
import com.example.movie_backend.repository.FranchiseRepository;
import com.example.movie_backend.service.FranchiseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FranchiseServiceImpl implements FranchiseService {
    FranchiseRepository franchiseRepository;
    FranchiseMapper franchiseMapper;

    @Override
    public FranchiseResponse create(FranchiseRequest request) {
        if (franchiseRepository.findByName(request.getName()).isPresent()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION); // Or more specific error
        }
        Franchise franchise = franchiseMapper.toFranchise(request);
        return franchiseMapper.toFranchiseResponse(franchiseRepository.save(franchise));
    }

    @Override
    public PageResponse<FranchiseResponse> getAll(int page, int size) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = franchiseRepository.findAll(pageable);

        return PageResponse.<FranchiseResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(franchiseMapper::toFranchiseResponse).toList())
                .build();
    }

    @Override
    public FranchiseResponse getById(String id) {
        return franchiseMapper.toFranchiseResponse(franchiseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)));
    }

    @Override
    public FranchiseResponse update(String id, FranchiseRequest request) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        franchiseMapper.updateFranchise(franchise, request);
        return franchiseMapper.toFranchiseResponse(franchiseRepository.save(franchise));
    }

    @Override
    public void delete(String id) {
        franchiseRepository.deleteById(id);
    }
}
