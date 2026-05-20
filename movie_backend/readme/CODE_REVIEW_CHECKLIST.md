# Checklist Các Vấn Đề Cần Khắc Phục Trong Source Code Backend

## Bug Tiềm Ẩn

1. **GlobalExceptionHandler.java**: Method `handlingRuntimeException` khai báo catch `Exception.class` nhưng parameter là `RuntimeException`. Điều này có thể gây confuse và không handle đúng tất cả exceptions.
2. **MovieRequest.java**: Validation messages `"TYPE_INVALID"` và `"STATUS_INVALID"` không tồn tại trong enum `ErrorCode`, dẫn đến `IllegalArgumentException` trong `GlobalExceptionHandler.handlingValidation`.
3. **MovieServiceImpl.java**: Trong `getTopMovies`, `type.toLowerCase()` có thể throw `NullPointerException` nếu `type` là null.
4. **MovieServiceImpl.java**: Trong `getTopMovies`, `totalElements` được set là `(long) data.size()`, nhưng `data` chỉ là list của page hiện tại, không phải tổng số elements, dẫn đến pagination sai.
5. **CommonController.java**: Endpoint `incrementMovieViews` không yêu cầu authentication, có thể bị spam để tăng views giả tạo.
6. **ErrorCode.java**: `INVALID_KEY` có message `"Uncategorized error"` thay vì mô tả đúng lỗi.
7. **UserServiceImpl.java**: Trong `createRequest`, không check `existsByEmail`, cho phép tạo user với email duplicate mặc dù entity có unique constraint.
8. **UserController.java**: Method `updateProfile` gọi `getMyInfo()` để lấy `currentUserId`, nhưng nếu user chưa authenticate, `getMyInfo()` sẽ throw exception, dẫn đến lỗi không mong muốn.
9. **AuthenticationServiceImpl.java**: Method `authenticate` tạo new `BCryptPasswordEncoder` mỗi lần thay vì inject bean, gây performance overhead và inconsistency.
10. **CustomJwtDecoder.java**: `nimbusJwtDecoder` lazy init nhưng không synchronized, có thể race condition trong multi-threaded environment.

## Vấn Đề Performance

1. **MovieRepository.java**: Query `searchMovies` sử dụng `DISTINCT` và `LEFT JOIN` với genres, có thể chậm khi có nhiều movies và genres. Nên tối ưu index và xem xét query plan.
2. **MovieServiceImpl.java**: Trong `incrementViews`, mỗi lần increment đều save cả movie và MovieView, có thể gây nhiều I/O nếu concurrent cao. Nên xem xét batch hoặc cache.
3. **MovieServiceImpl.java**: `getTopMovies` với type "all time" gọi `findAll(pageable)` với sort, nhưng không cần thiết nếu đã có index trên views.
4. **GlobalExceptionHandler.java**: Catch quá rộng `Exception.class` có thể che giấu bugs và làm log không chính xác.
5. **FileStorageServiceImpl.java**: Method `storeFile` không validate file size hoặc type, có thể dẫn đến out of memory hoặc security issues với malicious files.

## Code Smell

1. **MovieController.java**: Sử dụng fully qualified class names như `com.example.movie_backend.dto.response.ApiResponse` thay vì import, làm code dài dòng và khó đọc.
2. **MovieServiceImpl.java**: Logic set genres, studio, franchise được duplicate trong `create` và `update`.
3. **CommonController.java**: Nhiều endpoints trả về `ApiResponse<PageResponse<...>>` với builder dài dòng, có thể refactor thành method helper.
4. **Application.properties**: Hardcode password database, nên dùng environment variables.
5. **MovieServiceImpl.java**: Trong `getTopMovies`, comment "Simplified" cho totalPages, nhưng không nên simplified trong production.
6. **Repository queries**: Sử dụng string literals trong @Query, nên dùng named queries hoặc constants để maintainability.
7. **BaseEntity.java**: Sử dụng `String` cho id với `GenerationType.UUID`, ok nhưng có thể dùng `UUID` type.
8. **SecurityConfig.java**: PUBLIC_ENDPOINTS array có duplicate paths như "/users" và "/auth/\*\*", nhưng "/users" chỉ permit POST.
9. **FileStorageServiceImpl.java**: Method `deleteFile` catch `IOException` nhưng không log, làm khó debug file deletion failures.
10. **AuditLogServiceImpl.java**: Method `log` không check if `request` is null, có thể NPE nếu HttpServletRequest not available.
11. **WebMvcConfig.java**: Expose `/uploads/**` without authentication, có thể access sensitive files nếu path traversal.

## Các Vấn Đề Khác

1. **Validation**: Không validate `page` và `size` parameters trong controllers, có thể âm hoặc quá lớn.
2. **Error Handling**: Không có rate limiting cho endpoints public như increment views.
3. **Logging**: AuditLogService log với string concat, có thể dùng parameterized logging.
4. **Concurrency**: incrementViews không handle concurrent updates tốt, có thể dùng optimistic locking hoặc atomic operations.
5. **Testing**: Không thấy tests cho services, chỉ có test class trống.
6. **Configuration**: ddl-auto=update ok cho dev, nhưng production nên none hoặc validate.
7. **Dependencies**: MapStruct version cũ (1.5.5), nên update.
8. **Enums**: MovieType, MovieStatus không có descriptions hoặc validation thêm.
9. **Security**: File storage không validate file extensions, có thể upload executable files.
10. **Resource Management**: FileStorageServiceImpl không limit concurrent file operations, có thể exhaust resources.
