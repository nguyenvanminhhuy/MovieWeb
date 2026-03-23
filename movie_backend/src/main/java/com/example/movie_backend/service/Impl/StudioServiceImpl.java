package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.StudioRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.StudioResponse;
import com.example.movie_backend.entity.Studio;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.StudioMapper;
import com.example.movie_backend.repository.StudioRepository;
import com.example.movie_backend.service.StudioService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudioServiceImpl implements StudioService {
    StudioRepository studioRepository;
    StudioMapper studioMapper;

    @Override
    public StudioResponse create(StudioRequest request) {
        Studio studio = studioMapper.toStudio(request);
        return studioMapper.toStudioResponse(studioRepository.save(studio));
    }

    @Override
    public List<StudioResponse> getAll() {
        return studioRepository.findAll().stream()
                .map(studioMapper::toStudioResponse)
                .toList();
    }

    @Override
    public PageResponse<StudioResponse> getAllPaginated(int page, int size) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = studioRepository.findAll(pageable);

        return PageResponse.<StudioResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(studioMapper::toStudioResponse).toList())
                .build();
    }

    @Override
    public StudioResponse getById(String id) {
        return studioMapper.toStudioResponse(studioRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)));
    }

    @Override
    public StudioResponse update(String id, StudioRequest request) {
        Studio studio = studioRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        studioMapper.updateStudio(studio, request);
        return studioMapper.toStudioResponse(studioRepository.save(studio));
    }

    @Override
    public void delete(String id) {
        studioRepository.deleteById(id);
    }
}
