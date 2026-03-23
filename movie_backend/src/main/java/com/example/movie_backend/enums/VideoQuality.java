package com.example.movie_backend.enums;

public enum VideoQuality {
    P360("360p"),
    P480("480p"),
    P720("720p"),
    P1080("1080p");

    private final String value;

    VideoQuality(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
