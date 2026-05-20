package com.example.movie_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    MOVIE_NOT_FOUND(1009, "Movie not found", HttpStatus.NOT_FOUND),
    INVALID_EMAIL(1010, "Invalid email format", HttpStatus.BAD_REQUEST),
    STUDIO_NOT_FOUND(1011, "Studio not found", HttpStatus.NOT_FOUND),
    FRANCHISE_NOT_FOUND(1012, "Franchise not found", HttpStatus.NOT_FOUND),
    GENRE_NOT_FOUND(1013, "Genre not found", HttpStatus.NOT_FOUND),
    EPISODE_NOT_FOUND(1014, "Episode not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(1015, "Comment not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND(1016, "Role not found", HttpStatus.NOT_FOUND),
    MOVIE_ID_REQUIRED(1017, "Movie ID is required", HttpStatus.BAD_REQUEST),
    CONTENT_REQUIRED(1018, "Content is required", HttpStatus.BAD_REQUEST),
    EMAIL_REQUIRED(1019, "Email is required", HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED(1020, "Username is required", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1021, "Password is required", HttpStatus.BAD_REQUEST),
    EPISODE_ID_REQUIRED(1022, "Episode ID is required", HttpStatus.BAD_REQUEST),
    PROGRESS_INVALID(1023, "Progress must be between 0 and 100", HttpStatus.BAD_REQUEST),
    RATING_INVALID(1024, "Rating must be between 1 and 10", HttpStatus.BAD_REQUEST),
    TITLE_REQUIRED(1025, "Title is required", HttpStatus.BAD_REQUEST),
    DESCRIPTION_REQUIRED(1026, "Description is required", HttpStatus.BAD_REQUEST),
    EPISODE_NUMBER_INVALID(1027, "Episode number is invalid", HttpStatus.BAD_REQUEST),
    NAME_REQUIRED(1028, "Name is required", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
