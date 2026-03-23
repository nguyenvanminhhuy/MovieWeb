package com.example.movie_backend.service.impl;

import com.example.movie_backend.dto.response.NotificationResponse;
import com.example.movie_backend.dto.response.PageResponse;
import com.example.movie_backend.entity.Notification;
import com.example.movie_backend.entity.User;
import com.example.movie_backend.exception.AppException;
import com.example.movie_backend.exception.ErrorCode;
import com.example.movie_backend.repository.NotificationRepository;
import com.example.movie_backend.repository.UserRepository;
import com.example.movie_backend.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    UserRepository userRepository;

    @Override
    public PageResponse<NotificationResponse> getMyNotifications(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size);
        var pageData = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return PageResponse.<NotificationResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(n -> NotificationResponse.builder()
                                .id(n.getId())
                                .title(n.getTitle())
                                .message(n.getMessage())
                                .type(n.getType())
                                .targetUrl(n.getTargetUrl())
                                .read(n.isRead())
                                .createdAt(n.getCreatedAt())
                                .build())
                        .toList())
                .build();
    }

    @Override
    public long countUnread() {
        return notificationRepository.countByUserAndReadIsFalse(getCurrentUser());
    }

    @Override
    public void markAsRead(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead() {
        User user = getCurrentUser();
        List<Notification> unread = notificationRepository.findAll().stream()
                .filter(n -> n.getUser().getId().equals(user.getId()) && !n.isRead())
                .toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
