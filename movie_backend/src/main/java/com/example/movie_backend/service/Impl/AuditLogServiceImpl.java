package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.entity.AuditLog;
import com.example.movie_backend.repository.AuditLogRepository;
import com.example.movie_backend.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
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
public class AuditLogServiceImpl implements AuditLogService {
    AuditLogRepository auditLogRepository;
    HttpServletRequest request;

    @Override
    public void log(String action, String details) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser")) {
            username = "GUEST";
        }

        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(request.getRemoteAddr());

        auditLogRepository.save(log);
    }

    @Override
    public PageResponse<AuditLog> getAll(int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = auditLogRepository.findAll(pageable);

        return PageResponse.<AuditLog>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent())
                .build();
    }
}
