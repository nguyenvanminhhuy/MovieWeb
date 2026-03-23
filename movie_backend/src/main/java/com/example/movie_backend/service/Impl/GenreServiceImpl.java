package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.GenreRequest;
import com.example.movie_backend.dto.response.GenreResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.entity.Genre;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.GenreMapper;
import com.example.movie_backend.repository.GenreRepository;
import com.example.movie_backend.service.GenreService;
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
public class GenreServiceImpl implements GenreService {
    GenreRepository genreRepository;
    GenreMapper genreMapper;

    @Override
    public GenreResponse create(GenreRequest request) {
        Genre genre = genreMapper.toGenre(request);
        return genreMapper.toGenreResponse(genreRepository.save(genre));
    }

    @Override
    public List<GenreResponse> getAll() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toGenreResponse)
                .toList();
    }

    @Override
    public PageResponse<GenreResponse> getAllPaginated(int page, int size) {
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = genreRepository.findAll(pageable);

        return PageResponse.<GenreResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(genreMapper::toGenreResponse).toList())
                .build();
    }

    @Override
    public GenreResponse getById(String id) {
        return genreMapper.toGenreResponse(genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION))); // Should add GENRE_NOT_FOUND
    }

    @Override
    public GenreResponse update(String id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        genreMapper.updateGenre(genre, request);
        return genreMapper.toGenreResponse(genreRepository.save(genre));
    }

    @Override
    public void delete(String id) {
        genreRepository.deleteById(id);
    }
}
