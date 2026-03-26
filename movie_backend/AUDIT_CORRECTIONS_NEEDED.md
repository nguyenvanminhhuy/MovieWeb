# 🔧 API Audit - Implementation Corrections Required

## Overview

**Total Issues Found:** 3 critical authentication issues requiring code fixes

---

## Critical Issue #1: CommentController - Missing Authentication on POST /comments

**File:** `src/main/java/com/example/movie_backend/controller/CommentController.java`  
**Lines:** 19-22  
**Severity:** HIGH - Security Issue

### Current Code

```java
@Operation(summary = "Viết bình luận mới")
@PostMapping
ApiResponse<CommentResponse> create(@RequestBody CommentRequest request) {
    return ApiResponse.<CommentResponse>builder()
            .result(commentService.create(request))
            .build();
}
```

### Issue

- No `@PreAuthorize` annotation present
- Documentation states "Authentication: ✅ Yes"
- Endpoint should require user to be authenticated before posting comments

### Required Fix

Add `@PreAuthorize("isAuthenticated()")` annotation:

```java
@Operation(summary = "Viết bình luận mới")
@PostMapping
@PreAuthorize("isAuthenticated()")
ApiResponse<CommentResponse> create(@RequestBody CommentRequest request) {
    return ApiResponse.<CommentResponse>builder()
            .result(commentService.create(request))
            .build();
}
```

---

## Critical Issue #2: CommentController - Missing Authentication on POST /comments/{id}/like

**File:** `src/main/java/com/example/movie_backend/controller/CommentController.java`  
**Lines:** 25-29  
**Severity:** HIGH - Security Issue

### Current Code

```java
@Operation(summary = "Thích bình luận")
@PostMapping("/{id}/like")
ApiResponse<CommentResponse> like(@PathVariable String id) {
    return ApiResponse.<CommentResponse>builder()
            .result(commentService.likeComment(id))
            .build();
}
```

### Issue

- No `@PreAuthorize` annotation present
- Documentation states "Authentication: ✅ Yes"
- Endpoint should require user to be authenticated before liking comments

### Required Fix

Add `@PreAuthorize("isAuthenticated()")` annotation:

```java
@Operation(summary = "Thích bình luận")
@PostMapping("/{id}/like")
@PreAuthorize("isAuthenticated()")
ApiResponse<CommentResponse> like(@PathVariable String id) {
    return ApiResponse.<CommentResponse>builder()
            .result(commentService.likeComment(id))
            .build();
}
```

---

## Critical Issue #3: ReviewController - Missing Authentication on POST /reviews

**File:** `src/main/java/com/example/movie_backend/controller/ReviewController.java`  
**Lines:** 19-23  
**Severity:** HIGH - Security Issue

### Current Code

```java
@Operation(summary = "Viết đánh giá mới")
@PostMapping
ApiResponse<ReviewResponse> create(@RequestBody @Valid ReviewRequest request) {
    return ApiResponse.<ReviewResponse>builder()
            .result(reviewService.create(request))
            .build();
}
```

### Issue

- No `@PreAuthorize` annotation present
- Documentation states "Authentication: ✅ Yes"
- Endpoint should require user to be authenticated before posting reviews

### Required Fix

Add `@PreAuthorize("isAuthenticated()")` annotation:

```java
@Operation(summary = "Viết đánh giá mới")
@PostMapping
@PreAuthorize("isAuthenticated()")
ApiResponse<ReviewResponse> create(@RequestBody @Valid ReviewRequest request) {
    return ApiResponse.<ReviewResponse>builder()
            .result(reviewService.create(request))
            .build();
}
```

---

## Summary of Changes

| File                   | Method   | Current | Required                           | Impact   |
| ---------------------- | -------- | ------- | ---------------------------------- | -------- |
| CommentController.java | create() | No auth | @PreAuthorize("isAuthenticated()") | Security |
| CommentController.java | like()   | No auth | @PreAuthorize("isAuthenticated()") | Security |
| ReviewController.java  | create() | No auth | @PreAuthorize("isAuthenticated()") | Security |

---

## Implementation Steps

1. **Open** `CommentController.java`
2. **Locate** the `create()` method (around line 19)
3. **Add** `@PreAuthorize("isAuthenticated()")` before the method signature
4. **Verify** imports include `org.springframework.security.access.prepost.PreAuthorize`

5. **Locate** the `like()` method (around line 25)
6. **Add** `@PreAuthorize("isAuthenticated()")` before the method signature

7. **Open** `ReviewController.java`
8. **Locate** the `create()` method (around line 19)
9. **Add** `@PreAuthorize("isAuthenticated()")` before the method signature

10. **Compile** and test to ensure no startup errors
11. **Test** endpoints with and without authentication tokens

---

## Validation Checklist

After implementing fixes, verify:

- [ ] Code compiles without errors
- [ ] Application starts successfully
- [ ] POST /comments endpoint returns 403 Forbidden when not authenticated
- [ ] POST /comments/{id}/like endpoint returns 403 Forbidden when not authenticated
- [ ] POST /reviews endpoint returns 403 Forbidden when not authenticated
- [ ] All three endpoints work correctly with valid JWT token in Authorization header
- [ ] Swagger/OpenAPI documentation reflects authentication requirement
- [ ] All existing unit tests still pass
- [ ] New integration tests added for authentication validation

---

## Additional Recommendations

### 1. Update Swagger Configuration

Ensure Swagger/OpenAPI configuration includes:

- `@SecurityRequirement` annotation if using OpenAPI 3.1
- Security scheme configuration for JWT tokens

### 2. Add JavaDoc Comments

Add documentation to explain authentication requirements:

```java
/**
 * Write a new comment on a movie.
 * Requires authentication - user must be logged in.
 *
 * @param request the comment request with movieId and content
 * @return the created comment with metadata
 */
@PreAuthorize("isAuthenticated()")
@PostMapping
ApiResponse<CommentResponse> create(@RequestBody CommentRequest request)
```

### 3. Add Integration Tests

Create tests to verify authentication requirements:

```java
@Test
void testCreateCommentRequiresAuthentication() {
    // Should return 403 without token
    // Should succeed with valid token
}

@Test
void testLikeCommentRequiresAuthentication() {
    // Should return 403 without token
    // Should succeed with valid token
}
```

---

## Files Modified

- `src/main/java/com/example/movie_backend/controller/CommentController.java` (2 methods)
- `src/main/java/com/example/movie_backend/controller/ReviewController.java` (1 method)

---

**Report Date:** 2026-03-25  
**Priority:** CRITICAL  
**Timeline:** Implement immediately before next deployment
