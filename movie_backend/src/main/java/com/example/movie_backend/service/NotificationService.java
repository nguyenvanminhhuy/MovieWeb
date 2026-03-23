package com.example.movie_backend.service;

import com.example.movie_backend.dto.response.NotificationResponse;
import com.example.movie_backend.dto.response.PageResponse;

public interface NotificationService {
    PageResponse<NotificationResponse> getMyNotifications(int page, int size);
    long countUnread();
    void markAsRead(String id);
    void markAllAsRead();
}
