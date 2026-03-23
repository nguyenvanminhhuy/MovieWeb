package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.request.ReportRequest;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.dto.response.ReportResponse;
import com.example.movie_backend.entity.Report;
import com.example.movie_backend.entity.User;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.mapper.ReportMapper;
import com.example.movie_backend.repository.EpisodeRepository;
import com.example.movie_backend.repository.MovieRepository;
import com.example.movie_backend.repository.ReportRepository;
import com.example.movie_backend.repository.UserRepository;
import com.example.movie_backend.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {
    ReportRepository reportRepository;
    UserRepository userRepository;
    MovieRepository movieRepository;
    EpisodeRepository episodeRepository;
    ReportMapper reportMapper;

    @Override
    public ReportResponse create(ReportRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Report report = reportMapper.toReport(request);
        report.setUser(user);

        if (request.getMovieId() != null) {
            report.setMovie(movieRepository.findById(request.getMovieId()).orElse(null));
        }
        if (request.getEpisodeId() != null) {
            report.setEpisode(episodeRepository.findById(request.getEpisodeId()).orElse(null));
        }

        return reportMapper.toReportResponse(reportRepository.save(report));
    }

    @Override
    public PageResponse<ReportResponse> getAll(int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = reportRepository.findAll(pageable);

        return PageResponse.<ReportResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(reportMapper::toReportResponse).toList())
                .build();
    }

    @Override
    public void resolve(String id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        report.setResolved(true);
        reportRepository.save(report);
    }
}
