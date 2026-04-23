# Tài liệu Chi tiết API - Anime Movie Website Backend

Chào mừng bạn đến với tài liệu API chi tiết cho hệ thống Backend Anime. Hệ thống được xây dựng trên **Spring Boot 3.4.2**, bảo mật bằng **JWT**, cung cấp tổng cộng **78 API** để vận hành toàn bộ trang web từ phía người dùng đến quản trị viên.

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`
- **Tổng số API hiện có**: **81**

---

## 1. Xác thực & Bảo mật (Authentication) - 4 API

| STT | Phương thức | Endpoint           | Chức năng chi tiết                                         |
| :-- | :---------- | :----------------- | :--------------------------------------------------------- |
| 1   | `POST`      | `/auth/login`      | Xác thực tài khoản và trả về Access Token & Refresh Token. |
| 2   | `POST`      | `/auth/introspect` | Kiểm tra tính hợp lệ và thời gian hết hạn của Token.       |
| 3   | `POST`      | `/auth/logout`     | Đăng xuất, hủy hiệu lực của Token hiện tại.                |
| 4   | `POST`      | `/auth/refresh`    | Sử dụng Refresh Token để lấy Access Token mới.             |

---

## 2. API Công khai (Guest/Common) - 15 API

_Dành cho người xem phim, không yêu cầu đăng nhập._

| STT | Phương thức | Endpoint                                    | Chức năng chi tiết                                       |
| :-- | :---------- | :------------------------------------------ | :------------------------------------------------------- |
| 5   | `GET`       | `/common/movies`                            | Lấy danh sách phim (hỗ trợ lọc theo tên, thể loại, loại, trạng thái). |
| 6   | `GET`       | `/common/movies/{id}`                       | Lấy toàn bộ thông tin chi tiết của một bộ phim.          |
| 7   | `GET`       | `/common/movies/search`                     | Tìm kiếm phim nâng cao theo nhiều tiêu chí đồng thời.    |
| 8   | `GET`       | `/common/movies/types`                      | Lấy danh sách các loại phim hợp lệ (TV_SERIES, MOVIE...).|
| 9   | `GET`       | `/common/movies/statuses`                   | Lấy danh sách các trạng thái phim (ONGOING, COMPLETED...).|
| 10  | `GET`       | `/common/movies/top`                        | Lấy danh sách phim xu hướng (Hot) theo ngày/tuần/tháng.  |
| 11  | `GET`       | `/common/movies/{id}/related`               | Gợi ý các phim tương tự dựa trên thể loại.               |
| 12  | `POST`      | `/common/movies/{id}/views`                 | Ghi nhận lượt xem phim (tự động đếm lượt xem hàng ngày). |
| 13  | `GET`       | `/common/genres`                            | Lấy danh sách tất cả các thể loại anime.                 |
| 14  | `GET`       | `/common/studios`                           | Lấy danh sách các studio sản xuất phim.                  |
| 15  | `GET`       | `/common/episodes/movie/{movieId}`          | Lấy danh sách tập phim của bộ phim.                      |
| 16  | `GET`       | `/common/video-sources/episode/{episodeId}` | Lấy các link streaming (Server) của tập phim.            |
| 17  | `GET`       | `/common/subtitles/episode/{episodeId}`     | Lấy danh sách phụ đề (VietSub, EngSub...) của tập phim.  |
| 18  | `GET`       | `/common/comments/movie/{movieId}`          | Lấy danh sách các bình luận công khai của phim.          |
| 19  | `GET`       | `/common/reviews/movie/{movieId}`           | Lấy danh sách các bài đánh giá và điểm số của phim.      |

---

## 3. Quản lý Người dùng & Cá nhân (User) - 8 API

_Yêu cầu đăng nhập._

| STT | Phương thức | Endpoint                 | Chức năng chi tiết                            |
| :-- | :---------- | :----------------------- | :-------------------------------------------- |
| 18  | `GET`       | `/users/my-info`         | Lấy thông tin tài khoản của chính mình.       |
| 19  | `PUT`       | `/users/update-profile`  | Tự cập nhật Họ tên, Avatar và Mật khẩu.       |
| 20  | `POST`      | `/users`                 | Đăng ký tài khoản (Mặc định quyền USER).      |
| 21  | `GET`       | `/users`                 | Admin xem danh sách toàn bộ người dùng.       |
| 22  | `GET`       | `/users/{userId}`        | Admin xem chi tiết thông tin một người dùng.  |
| 23  | `PUT`       | `/users/{userId}`        | Admin cập nhật thông tin người dùng.          |
| 24  | `DELETE`    | `/users/{userId}`        | Admin xóa vĩnh viễn tài khoản người dùng.     |
| 25  | `PATCH`     | `/users/{userId}/status` | Admin khóa hoặc mở khóa tài khoản người dùng. |

---

## 4. Tương tác: Yêu thích & Lịch sử - 5 API

| STT | Phương thức | Endpoint               | Chức năng chi tiết                                  |
| :-- | :---------- | :--------------------- | :-------------------------------------------------- |
| 26  | `POST`      | `/favorites/{movieId}` | Thêm phim vào danh sách yêu thích cá nhân.          |
| 27  | `DELETE`    | `/favorites/{movieId}` | Xóa phim khỏi danh sách yêu thích.                  |
| 28  | `GET`       | `/favorites`           | Xem danh sách phim yêu thích của tôi (phân trang).  |
| 29  | `POST`      | `/history`             | Lưu lại tập phim và thời gian đang xem dở.          |
| 30  | `GET`       | `/history`             | Xem lại danh sách phim đã xem gần đây (phân trang). |

---

## 5. Tương tác: Bình luận & Đánh giá - 5 API

| STT | Phương thức | Endpoint              | Chức năng chi tiết                                  |
| :-- | :---------- | :-------------------- | :-------------------------------------------------- |
| 31  | `POST`      | `/comments`           | Gửi bình luận mới hoặc trả lời bình luận khác.      |
| 32  | `POST`      | `/comments/{id}/like` | Thích (Like) một bình luận.                         |
| 33  | `DELETE`    | `/comments/{id}`      | Xóa bình luận của mình (Admin có quyền xóa tất cả). |
| 34  | `POST`      | `/reviews`            | Gửi đánh giá Rating và nhận xét về phim.            |
| 35  | `DELETE`    | `/reviews/{id}`       | Admin xóa bài đánh giá vi phạm.                     |

---

## 6. Quản lý Phim (Admin Movie) - 5 API

| STT | Phương thức | Endpoint       | Chức năng chi tiết                          |
| :-- | :---------- | :------------- | :------------------------------------------ |
| 36  | `GET`       | `/movies`      | Lấy danh sách phim (hỗ trợ lọc theo tên, thể loại...). |
| 37  | `GET`       | `/movies/{id}` | Xem chi tiết phim trong trang quản trị.     |
| 38  | `POST`      | `/movies`      | Thêm mới một bộ phim anime (Ghi log Audit). |
| 39  | `PUT`       | `/movies/{id}` | Cập nhật thông tin phim.                    |
| 40  | `DELETE`    | `/movies/{id}` | Xóa phim khỏi hệ thống.                     |

---

## 7. Quản lý Tập phim (Admin Episode) - 4 API

| STT | Phương thức | Endpoint                    | Chức năng chi tiết                                  |
| :-- | :---------- | :-------------------------- | :-------------------------------------------------- |
| 41  | `POST`      | `/episodes`                 | Thêm tập phim mới (Tự động gửi thông báo cho User). |
| 42  | `GET`       | `/episodes/movie/{movieId}` | Danh sách tập phim để Admin quản lý.                |
| 43  | `PUT`       | `/episodes/{id}`            | Cập nhật thông tin tập phim.                        |
| 44  | `DELETE`    | `/episodes/{id}`            | Xóa tập phim.                                       |

---

## 8. Quản lý Thể loại, Studio & Franchise - 13 API

| STT | Phương thức | Endpoint           | Chức năng chi tiết                 |
| :-- | :---------- | :----------------- | :--------------------------------- |
| 45  | `POST`      | `/genres`          | Tạo thể loại phim mới.             |
| 46  | `GET`       | `/genres`          | Danh sách thể loại (Admin).        |
| 47  | `PUT`       | `/genres/{id}`     | Cập nhật tên thể loại.             |
| 48  | `DELETE`    | `/genres/{id}`     | Xóa thể loại.                      |
| 49  | `POST`      | `/studios`         | Thêm studio sản xuất mới.          |
| 50  | `GET`       | `/studios`         | Danh sách studio (Admin).          |
| 51  | `PUT`       | `/studios/{id}`    | Cập nhật thông tin studio.         |
| 52  | `DELETE`    | `/studios/{id}`    | Xóa studio.                        |
| 53  | `POST`      | `/franchises`      | Tạo chuỗi phim (Vũ trụ anime) mới. |
| 54  | `GET`       | `/franchises`      | Lấy danh sách chuỗi phim.          |
| 55  | `GET`       | `/franchises/{id}` | Chi tiết một chuỗi phim.           |
| 56  | `PUT`       | `/franchises/{id}` | Cập nhật chuỗi phim.               |
| 57  | `DELETE`    | `/franchises/{id}` | Xóa chuỗi phim.                    |

---

## 9. Quản lý Media, Phụ đề & Nguồn video - 7 API

| STT | Phương thức | Endpoint                             | Chức năng chi tiết                      |
| :-- | :---------- | :----------------------------------- | :-------------------------------------- |
| 58  | `POST`      | `/media/upload`                      | Tải ảnh lên server (Poster, Avatar...). |
| 59  | `POST`      | `/video-sources`                     | Thêm link streaming cho tập phim.       |
| 60  | `GET`       | `/video-sources/episode/{episodeId}` | Xem các nguồn video của tập.            |
| 61  | `DELETE`    | `/video-sources/{id}`                | Xóa nguồn video.                        |
| 62  | `POST`      | `/subtitles`                         | Thêm phụ đề mới cho tập phim.           |
| 63  | `GET`       | `/subtitles/episode/{episodeId}`     | Xem các phụ đề của tập.                 |
| 64  | `DELETE`    | `/subtitles/{id}`                    | Xóa phụ đề.                             |

---

## 10. Thông báo & Báo cáo (Notifications & Reports) - 7 API

| STT | Phương thức | Endpoint                      | Chức năng chi tiết                            |
| :-- | :---------- | :---------------------------- | :-------------------------------------------- |
| 65  | `GET`       | `/notifications`              | Lấy danh sách thông báo của tôi (phân trang). |
| 66  | `GET`       | `/notifications/unread-count` | Lấy số lượng thông báo chưa đọc.              |
| 67  | `PATCH`     | `/notifications/{id}/read`    | Đánh dấu một thông báo đã xem.                |
| 68  | `PATCH`     | `/notifications/read-all`     | Đánh dấu tất cả thông báo là đã xem.          |
| 69  | `POST`      | `/reports`                    | Gửi báo cáo lỗi tập phim/vi phạm.             |
| 70  | `GET`       | `/reports`                    | Admin xem danh sách các báo cáo.              |
| 71  | `PATCH`     | `/reports/{id}/resolve`       | Admin đánh dấu đã xử lý xong báo cáo.         |

---

## 11. Thống kê & Phân quyền (RBAC) - 8 API

| STT | Method   | Endpoint                    | Chức năng chi tiết    | Đầu vào (Input)                | Đầu ra (Output)          |
| :-- | :------- | :-------------------------- | :-------------------- | :----------------------------- | :----------------------- |
| 72  | `GET`    | `/admin/stats/dashboard`    | Thống kê Dashboard    | `Admin`                        | `DashboardResponse`      |
| 73  | `POST`   | `/roles`                    | Tạo vai trò mới       | `JSON: {name, permissions...}` | `RoleResponse`           |
| 74  | `GET`    | `/roles`                    | Danh sách vai trò     | `-`                            | `List<RoleResponse>`     |
| 75  | `DELETE` | `/roles/{role}`             | Xóa vai trò           | `Path: role`                   | `void`                   |
| 76  | `POST`   | `/permissions`              | Tạo quyền mới         | `JSON: {name...}`              | `PermisResponse`         |
| 77  | `GET`    | `/permissions`              | Danh sách quyền       | `-`                            | `List<PermisResponse>`   |
| 78  | `DELETE` | `/permissions/{permission}` | Xóa quyền             | `Path: permission`             | `void`                   |
| 79  | `GET`    | `/admin/audit-logs`         | Xem lịch sử hoạt động | `Admin, Query: page, size`     | `PageResponse<AuditLog>` |

---

**Lưu ý kỹ thuật**:

- Tất cả các API trả về dữ liệu đều bọc trong lớp `ApiResponse` với mã thành công là `1000`.
- Các API quản trị yêu cầu quyền `ADMIN`.
- Các tệp tin tải lên được lưu tại thư mục `/uploads` và có thể truy cập trực tiếp qua URL.
