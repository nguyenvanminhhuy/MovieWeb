# 📚 API Endpoints Documentation - Movie Backend

**Ngày cập nhật:** 25/03/2026
**Phiên bản:** 1.1

---

## 📋 Table of Contents

1. [Authentication API](#-authentication-api)
2. [User Management API](#-user-management-api)
3. [Movie Management API](#-movie-management-api)
4. [Common API (Public)](#-common-api-public)
5. [Genre Management API](#-genre-management-api)
6. [Studio Management API](#-studio-management-api)
7. [Episode Management API](#-episode-management-api)
8. [Video Source & Subtitles API](#-video-source--subtitles-api)
9. [Comment & Review API](#-comment--review-api)
10. [Favorite Management API](#-favorite-management-api)
11. [Watch History API](#-watch-history-api)
12. [Franchise Management API](#-franchise-management-api)
13. [Role & Permission API](#-role--permission-api)
14. [Report Management API](#-report-management-api)
15. [Notification API](#-notification-api)
16. [Statistics API](#-statistics-api)
17. [Media Upload API](#-media-upload-api)
18. [Audit Log API](#-audit-log-api)

---

## 🔐 Authentication API

**Base URL:** `/auth`
**Description:** Xác thực người dùng và quản lý Token

### 1. Login (Đăng nhập)

- **HTTP Method:** `POST`
- **Endpoint:** `/auth/login`
- **Summary:** Đăng nhập hệ thống - Trả về Token JWT nếu thông tin đăng nhập chính xác
- **Authentication:** ❌ No
- **Role Required:** None

#### Request Body

```json
{
  "username": "string",
  "password": "string"
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "authenticated": true
  }
}
```

---

### 2. Introspect Token (Kiểm tra Token)

- **HTTP Method:** `POST`
- **Endpoint:** `/auth/introspect`
- **Summary:** Kiểm tra tính hợp lệ của Token - Kiểm tra xem Token JWT còn hiệu lực hay không
- **Authentication:** ❌ No
- **Role Required:** None

#### Request Body

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "valid": true
  }
}
```

---

### 3. Logout (Đăng xuất)

- **HTTP Method:** `POST`
- **Endpoint:** `/auth/logout`
- **Summary:** Đăng xuất tài khoản - Vô hiệu hóa Token hiện tại
- **Authentication:** ✅ Yes (Requires valid JWT)
- **Role Required:** None

#### Request Body

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### 4. Refresh Token (Làm mới Token)

- **HTTP Method:** `POST`
- **Endpoint:** `/auth/refresh`
- **Summary:** Làm mới Token - Sử dụng Refresh Token để lấy Access Token mới
- **Authentication:** ❌ No
- **Role Required:** None

#### Request Body

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "authenticated": true
  }
}
```

---

## 👥 User Management API

**Base URL:** `/users`
**Description:** Quản lý người dùng (Users)

### 1. Get My Info (Lấy thông tin cá nhân)

- **HTTP Method:** `GET`
- **Endpoint:** `/users/my-info`
- **Summary:** Lấy thông tin cá nhân của người dùng đang đăng nhập
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Request Parameters

None

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatar": "string",
    "enabled": true,
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

### 2. Create User (Tạo người dùng)

- **HTTP Method:** `POST`
- **Endpoint:** `/users`
- **Summary:** Tạo mới người dùng
- **Authentication:** ❌ No
- **Role Required:** None

#### Request Body

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "fullName": "string"
}
```

#### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatar": "string",
    "enabled": true,
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

### 3. Get All Users (Lấy danh sách người dùng)

- **HTTP Method:** `GET`
- **Endpoint:** `/users`
- **Summary:** Lấy danh sách tất cả người dùng (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "data": [
      {
        "id": "string",
        "username": "string",
        "email": "string",
        "fullName": "string",
        "avatar": "string",
        "enabled": true,
        "createdAt": "2026-03-25T10:00:00Z"
      }
    ]
  }
}
```

---

### 4. Get User by ID (Lấy thông tin người dùng)

- **HTTP Method:** `GET`
- **Endpoint:** `/users/{userId}`
- **Summary:** Lấy thông tin người dùng theo ID
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description       |
| --------- | ------ | ----------------- |
| userId    | string | ID của người dùng |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatar": "string",
    "enabled": true,
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

### 5. Update User (Cập nhật người dùng)

- **HTTP Method:** `PUT`
- **Endpoint:** `/users/{userId}`
- **Summary:** Cập nhật thông tin người dùng
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description       |
| --------- | ------ | ----------------- |
| userId    | string | ID của người dùng |

#### Request Body

```json
{
  "email": "string",
  "fullName": "string",
  "avatar": "string"
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatar": "string",
    "enabled": true,
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

### 6. Delete User (Xóa người dùng)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/users/{userId}`
- **Summary:** Xóa người dùng
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description       |
| --------- | ------ | ----------------- |
| userId    | string | ID của người dùng |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": "User has been deleted"
}
```

---

### 7. Update Profile (Cập nhật hồ sơ cá nhân)

- **HTTP Method:** `PUT`
- **Endpoint:** `/users/update-profile`
- **Summary:** Cập nhật thông tin cá nhân
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Request Body

```json
{
  "email": "string",
  "fullName": "string",
  "avatar": "string"
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "username": "string",
    "email": "string",
    "fullName": "string",
    "avatar": "string",
    "enabled": true,
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

### 8. Set User Status (Khóa/Mở khóa tài khoản)

- **HTTP Method:** `PATCH`
- **Endpoint:** `/users/{userId}/status`
- **Summary:** Khóa/Mở khóa tài khoản người dùng
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description       |
| --------- | ------ | ----------------- |
| userId    | string | ID của người dùng |

#### Query Parameters

| Parameter | Type    | Description              |
| --------- | ------- | ------------------------ |
| enabled   | boolean | true=mở khóa, false=khóa |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 🎬 Movie Management API

**Base URL:** `/movies`
**Description:** Quản lý danh sách phim Anime (Admin)

### 1. Get All Movies (Lấy danh sách phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/movies`
- **Summary:** Lấy danh sách tất cả các bộ phim hiện có (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "data": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "posterUrl": "string",
        "bannerUrl": "string",
        "type": "SERIES|MOVIE|ONA",
        "status": "ONGOING|COMPLETED|UPCOMING",
        "episodeCount": 0,
        "releaseDate": "2026-03-25",
        "views": 0,
        "rating": 0.0
      }
    ]
  }
}
```

---

### 2. Get Movie by ID (Lấy thông tin phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/movies/{id}`
- **Summary:** Lấy thông tin phim theo ID
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| id        | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "title": "string",
    "description": "string",
    "posterUrl": "string",
    "bannerUrl": "string",
    "type": "SERIES|MOVIE|ONA",
    "status": "ONGOING|COMPLETED|UPCOMING",
    "episodeCount": 0,
    "releaseDate": "2026-03-25",
    "views": 0,
    "rating": 0.0
  }
}
```

---

### 3. Create Movie (Tạo phim)

- **HTTP Method:** `POST`
- **Endpoint:** `/movies`
- **Summary:** Tạo mới phim - Thêm một bộ phim anime mới vào hệ thống
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Request Body

```json
{
  "title": "string",
  "description": "string",
  "posterUrl": "string",
  "bannerUrl": "string",
  "type": "SERIES|MOVIE|ONA",
  "status": "ONGOING|COMPLETED|UPCOMING",
  "releaseDate": "2026-03-25",
  "studios": ["string"],
  "genres": ["string"]
}
```

#### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "title": "string",
    "description": "string",
    "posterUrl": "string",
    "bannerUrl": "string",
    "type": "SERIES|MOVIE|ONA",
    "status": "ONGOING|COMPLETED|UPCOMING",
    "episodeCount": 0,
    "releaseDate": "2026-03-25",
    "views": 0,
    "rating": 0.0
  }
}
```

---

### 4. Update Movie (Cập nhật phim)

- **HTTP Method:** `PUT`
- **Endpoint:** `/movies/{id}`
- **Summary:** Cập nhật thông tin phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| id        | string | ID của phim |

#### Request Body

```json
{
  "title": "string",
  "description": "string",
  "posterUrl": "string",
  "bannerUrl": "string",
  "type": "SERIES|MOVIE|ONA",
  "status": "ONGOING|COMPLETED|UPCOMING",
  "releaseDate": "2026-03-25",
  "studios": ["string"],
  "genres": ["string"]
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "title": "string",
    "description": "string",
    "posterUrl": "string",
    "bannerUrl": "string",
    "type": "SERIES|MOVIE|ONA",
    "status": "ONGOING|COMPLETED|UPCOMING",
    "episodeCount": 0,
    "releaseDate": "2026-03-25",
    "views": 0,
    "rating": 0.0
  }
}
```

---

### 5. Delete Movie (Xóa phim)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/movies/{id}`
- **Summary:** Xóa phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| id        | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 🔍 Common API (Public)

**Base URL:** `/common`
**Description:** Các API công khai dành cho khách (không cần đăng nhập)

### 1. Search & Filter Movies (Tìm kiếm và lọc phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/movies/search`
- **Summary:** Tìm kiếm và lọc phim nâng cao
- **Authentication:** ❌ No
- **Role Required:** None

#### Query Parameters

| Parameter   | Type   | Required | Description                              |
| ----------- | ------ | -------- | ---------------------------------------- |
| query       | string | No       | Từ khóa tìm kiếm                         |
| genreId     | string | No       | ID của thể loại                          |
| franchiseId | string | No       | ID của franchise                         |
| type        | string | No       | Loại phim: SERIES, MOVIE, ONA            |
| status      | string | No       | Trạng thái: ONGOING, COMPLETED, UPCOMING |
| page        | int    | No       | Số trang (default: 1)                    |
| size        | int    | No       | Số bản ghi trên trang (default: 10)      |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "data": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "posterUrl": "string",
        "bannerUrl": "string",
        "type": "SERIES|MOVIE|ONA",
        "status": "ONGOING|COMPLETED|UPCOMING",
        "episodeCount": 0,
        "releaseDate": "2026-03-25",
        "views": 0,
        "rating": 0.0
      }
    ]
  }
}
```

---

### 2. Get All Movies (Lấy danh sách phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/movies`
- **Summary:** Lấy danh sách phim (Phân trang)
- **Authentication:** ❌ No
- **Role Required:** None

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "data": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "posterUrl": "string",
        "bannerUrl": "string",
        "type": "SERIES|MOVIE|ONA",
        "status": "ONGOING|COMPLETED|UPCOMING",
        "episodeCount": 0,
        "releaseDate": "2026-03-25",
        "views": 0,
        "rating": 0.0
      }
    ]
  }
}
```

---

### 3. Get Movie by ID (Lấy thông tin phim theo ID)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/movies/{id}`
- **Summary:** Lấy thông tin phim theo ID
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| id        | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "title": "string",
    "description": "string",
    "posterUrl": "string",
    "bannerUrl": "string",
    "type": "SERIES|MOVIE|ONA",
    "status": "ONGOING|COMPLETED|UPCOMING",
    "episodeCount": 0,
    "releaseDate": "2026-03-25",
    "views": 0,
    "rating": 0.0
  }
}
```

---

### 4. Get Top Movies (Lấy top phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/movies/top`
- **Summary:** Lấy top phim (day, week, month, all)
- **Authentication:** ❌ No
- **Role Required:** None

#### Query Parameters

| Parameter | Type   | Default | Description                |
| --------- | ------ | ------- | -------------------------- |
| type      | string | all     | day, week, month, hoặc all |
| page      | int    | 1       | Số trang                   |
| size      | int    | 10      | Số bản ghi trên trang      |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "data": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "posterUrl": "string",
        "bannerUrl": "string",
        "type": "SERIES|MOVIE|ONA",
        "status": "ONGOING|COMPLETED|UPCOMING",
        "episodeCount": 0,
        "releaseDate": "2026-03-25",
        "views": 0,
        "rating": 0.0
      }
    ]
  }
}
```

---

### 5. Get Related Movies (Lấy phim liên quan)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/movies/{id}/related`
- **Summary:** Lấy danh sách phim liên quan
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| id        | string | ID của phim |

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 5       | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 5,
    "totalElements": 20,
    "totalPages": 4,
    "data": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "posterUrl": "string",
        "bannerUrl": "string",
        "type": "SERIES|MOVIE|ONA",
        "status": "ONGOING|COMPLETED|UPCOMING",
        "episodeCount": 0,
        "releaseDate": "2026-03-25",
        "views": 0,
        "rating": 0.0
      }
    ]
  }
}
```

---

### 6. Increment Movie Views (Tăng lượt xem)

- **HTTP Method:** `POST`
- **Endpoint:** `/common/movies/{id}/views`
- **Summary:** Tăng lượt xem cho phim
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| id        | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### 7. Get All Genres (Lấy danh sách thể loại)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/genres`
- **Summary:** Lấy danh sách thể loại (Phân trang)
- **Authentication:** ❌ No
- **Role Required:** None

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "data": [
      {
        "id": "string",
        "name": "string",
        "description": "string"
      }
    ]
  }
}
```

---

### 8. Get All Studios (Lấy danh sách studio)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/studios`
- **Summary:** Lấy danh sách studio (Phân trang)
- **Authentication:** ❌ No
- **Role Required:** None

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 30,
    "totalPages": 3,
    "data": [
      {
        "id": "string",
        "name": "string",
        "logoUrl": "string"
      }
    ]
  }
}
```

---

### 9. Get Episodes by Movie (Lấy tập phim theo phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/episodes/movie/{movieId}`
- **Summary:** Lấy danh sách tập phim theo bộ phim
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| movieId   | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "movieId": "string",
      "episodeNumber": 1,
      "title": "string",
      "description": "string",
      "duration": 0,
      "thumbnailUrl": "string"
    }
  ]
}
```

---

### 10. Get Video Sources by Episode (Lấy nguồn video theo tập)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/video-sources/episode/{episodeId}`
- **Summary:** Lấy danh sách nguồn video theo tập phim
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| episodeId | string | ID của tập phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "episodeId": "string",
      "videoUrl": "string",
      "quality": "720p|1080p|480p",
      "embedCode": "string"
    }
  ]
}
```

---

### 11. Get Subtitles by Episode (Lấy phụ đề theo tập)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/subtitles/episode/{episodeId}`
- **Summary:** Lấy danh sách phụ đề theo tập phim
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| episodeId | string | ID của tập phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "episodeId": "string",
      "language": "string",
      "subtitleUrl": "string"
    }
  ]
}
```

---

### 12. Get Comments by Movie (Lấy bình luận theo phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/comments/movie/{movieId}`
- **Summary:** Lấy danh sách bình luận theo phim
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| movieId   | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "movieId": "string",
      "userId": "string",
      "userName": "string",
      "content": "string",
      "likes": 0,
      "createdAt": "2026-03-25T10:00:00Z"
    }
  ]
}
```

---

### 13. Get Reviews by Movie (Lấy đánh giá theo phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/common/reviews/movie/{movieId}`
- **Summary:** Lấy danh sách đánh giá theo phim
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| movieId   | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "movieId": "string",
      "userId": "string",
      "userName": "string",
      "rating": 8.5,
      "content": "string",
      "createdAt": "2026-03-25T10:00:00Z"
    }
  ]
}
```

---

## 📂 Genre Management API

**Base URL:** `/genres`
**Description:** Quản lý thể loại phim (Admin)

### 1. Create Genre (Tạo thể loại)

- **HTTP Method:** `POST`
- **Endpoint:** `/genres`
- **Summary:** Tạo mới thể loại
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Request Body

```json
{
  "name": "string",
  "description": "string"
}
```

#### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "name": "string",
    "description": "string"
  }
}
```

---

### 2. Get All Genres (Lấy tất cả thể loại)

- **HTTP Method:** `GET`
- **Endpoint:** `/genres`
- **Summary:** Lấy tất cả thể loại (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "data": [
      {
        "id": "string",
        "name": "string",
        "description": "string"
      }
    ]
  }
}
```

---

### 3. Update Genre (Cập nhật thể loại)

- **HTTP Method:** `PUT`
- **Endpoint:** `/genres/{id}`
- **Summary:** Cập nhật thể loại
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| id        | string | ID của thể loại |

#### Request Body

```json
{
  "name": "string",
  "description": "string"
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "name": "string",
    "description": "string"
  }
}
```

---

### 4. Delete Genre (Xóa thể loại)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/genres/{id}`
- **Summary:** Xóa thể loại
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| id        | string | ID của thể loại |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 🏢 Studio Management API

**Base URL:** `/studios`
**Description:** Quản lý studio sản xuất (Admin)

### 1. Create Studio (Tạo studio)

- **HTTP Method:** `POST`
- **Endpoint:** `/studios`
- **Summary:** Tạo mới studio
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Request Body

```json
{
  "name": "string",
  "logoUrl": "string",
  "description": "string"
}
```

#### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "name": "string",
    "logoUrl": "string",
    "description": "string"
  }
}
```

---

### 2. Get All Studios (Lấy tất cả studio)

- **HTTP Method:** `GET`
- **Endpoint:** `/studios`
- **Summary:** Lấy tất cả studio (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 30,
    "totalPages": 3,
    "data": [
      {
        "id": "string",
        "name": "string",
        "logoUrl": "string",
        "description": "string"
      }
    ]
  }
}
```

---

### 3. Update Studio (Cập nhật studio)

- **HTTP Method:** `PUT`
- **Endpoint:** `/studios/{id}`
- **Summary:** Cập nhật studio
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description   |
| --------- | ------ | ------------- |
| id        | string | ID của studio |

#### Request Body

```json
{
  "name": "string",
  "logoUrl": "string",
  "description": "string"
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "name": "string",
    "logoUrl": "string",
    "description": "string"
  }
}
```

---

### 4. Delete Studio (Xóa studio)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/studios/{id}`
- **Summary:** Xóa studio
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description   |
| --------- | ------ | ------------- |
| id        | string | ID của studio |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 🎥 Episode Management API

**Base URL:** `/episodes`
**Description:** Quản lý tập phim (Admin)

### 1. Create Episode (Tạo tập phim)

- **HTTP Method:** `POST`
- **Endpoint:** `/episodes`
- **Summary:** Tạo mới tập phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Request Body

```json
{
  "movieId": "string",
  "episodeNumber": 1,
  "title": "string",
  "description": "string",
  "duration": 1440,
  "thumbnailUrl": "string"
}
```

#### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "movieId": "string",
    "episodeNumber": 1,
    "title": "string",
    "description": "string",
    "duration": 1440,
    "thumbnailUrl": "string"
  }
}
```

---

### 2. Get Episodes by Movie (Lấy tập phim theo phim)

- **HTTP Method:** `GET`
- **Endpoint:** `/episodes/movie/{movieId}`
- **Summary:** Lấy danh sách tập phim theo bộ phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| movieId   | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "movieId": "string",
      "episodeNumber": 1,
      "title": "string",
      "description": "string",
      "duration": 1440,
      "thumbnailUrl": "string"
    }
  ]
}
```

---

### 3. Update Episode (Cập nhật tập phim)

- **HTTP Method:** `PUT`
- **Endpoint:** `/episodes/{id}`
- **Summary:** Cập nhật tập phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| id        | string | ID của tập phim |

#### Request Body

```json
{
  "movieId": "string",
  "episodeNumber": 1,
  "title": "string",
  "description": "string",
  "duration": 1440,
  "thumbnailUrl": "string"
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "movieId": "string",
    "episodeNumber": 1,
    "title": "string",
    "description": "string",
    "duration": 1440,
    "thumbnailUrl": "string"
  }
}
```

---

### 4. Delete Episode (Xóa tập phim)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/episodes/{id}`
- **Summary:** Xóa tập phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| id        | string | ID của tập phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 🎬 Video Source & Subtitles API

### Video Source API

**Base URL:** `/video-sources`
**Description:** Quản lý nguồn video (Admin)

#### 1. Create Video Source (Thêm nguồn video)

- **HTTP Method:** `POST`
- **Endpoint:** `/video-sources`
- **Summary:** Thêm nguồn video mới
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Request Body

```json
{
  "episodeId": "string",
  "videoUrl": "string",
  "quality": "720p|1080p|480p",
  "embedCode": "string"
}
```

##### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "episodeId": "string",
    "videoUrl": "string",
    "quality": "720p|1080p|480p",
    "embedCode": "string"
  }
}
```

---

#### 2. Get Video Sources by Episode (Lấy nguồn video)

- **HTTP Method:** `GET`
- **Endpoint:** `/video-sources/episode/{episodeId}`
- **Summary:** Lấy danh sách nguồn video theo tập phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| episodeId | string | ID của tập phim |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "episodeId": "string",
      "videoUrl": "string",
      "quality": "720p|1080p|480p",
      "embedCode": "string"
    }
  ]
}
```

---

#### 3. Delete Video Source (Xóa nguồn video)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/video-sources/{id}`
- **Summary:** Xóa nguồn video
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Path Parameters

| Parameter | Type   | Description        |
| --------- | ------ | ------------------ |
| id        | string | ID của nguồn video |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### Subtitle API

**Base URL:** `/subtitles`
**Description:** Quản lý phụ đề (Admin)

#### 1. Create Subtitle (Thêm phụ đề)

- **HTTP Method:** `POST`
- **Endpoint:** `/subtitles`
- **Summary:** Thêm phụ đề mới
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Request Body

```json
{
  "episodeId": "string",
  "language": "string",
  "subtitleUrl": "string"
}
```

##### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "episodeId": "string",
    "language": "string",
    "subtitleUrl": "string"
  }
}
```

---

#### 2. Get Subtitles by Episode (Lấy phụ đề)

- **HTTP Method:** `GET`
- **Endpoint:** `/subtitles/episode/{episodeId}`
- **Summary:** Lấy danh sách phụ đề theo tập phim
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| episodeId | string | ID của tập phim |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "id": "string",
      "episodeId": "string",
      "language": "string",
      "subtitleUrl": "string"
    }
  ]
}
```

---

#### 3. Delete Subtitle (Xóa phụ đề)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/subtitles/{id}`
- **Summary:** Xóa phụ đề
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Path Parameters

| Parameter | Type   | Description   |
| --------- | ------ | ------------- |
| id        | string | ID của phụ đề |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 💬 Comment & Review API

### Comment API

**Base URL:** `/comments`
**Description:** Quản lý bình luận

#### 1. Create Comment (Viết bình luận)

- **HTTP Method:** `POST`
- **Endpoint:** `/comments`
- **Summary:** Viết bình luận mới
- **Authentication:** ✅ Yes
- **Role Required:** None

##### Request Body

```json
{
  "movieId": "string",
  "content": "string"
}
```

##### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "movieId": "string",
    "userId": "string",
    "userName": "string",
    "content": "string",
    "likes": 0,
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

#### 2. Like Comment (Thích bình luận)

- **HTTP Method:** `POST`
- **Endpoint:** `/comments/{id}/like`
- **Summary:** Thích bình luận
- **Authentication:** ✅ Yes
- **Role Required:** None

##### Path Parameters

| Parameter | Type   | Description      |
| --------- | ------ | ---------------- |
| id        | string | ID của bình luận |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "movieId": "string",
    "userId": "string",
    "userName": "string",
    "content": "string",
    "likes": 1,
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

#### 3. Delete Comment (Xóa bình luận)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/comments/{id}`
- **Summary:** Xóa bình luận
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN or Owner

##### Path Parameters

| Parameter | Type   | Description      |
| --------- | ------ | ---------------- |
| id        | string | ID của bình luận |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### Review API

**Base URL:** `/reviews`
**Description:** Quản lý đánh giá phim

#### 1. Create Review (Viết đánh giá)

- **HTTP Method:** `POST`
- **Endpoint:** `/reviews`
- **Summary:** Viết đánh giá mới
- **Authentication:** ✅ Yes
- **Role Required:** None

##### Request Body

```json
{
  "movieId": "string",
  "rating": 8.5,
  "content": "string"
}
```

##### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "movieId": "string",
    "userId": "string",
    "userName": "string",
    "rating": 8.5,
    "content": "string",
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

#### 2. Delete Review (Xóa đánh giá)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/reviews/{id}`
- **Summary:** Xóa đánh giá
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Path Parameters

| Parameter | Type   | Description     |
| --------- | ------ | --------------- |
| id        | string | ID của đánh giá |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## ❤️ Favorite Management API

**Base URL:** `/favorites`
**Description:** Quản lý phim yêu thích của người dùng

### 1. Add Favorite (Thêm vào yêu thích)

- **HTTP Method:** `POST`
- **Endpoint:** `/favorites/{movieId}`
- **Summary:** Thêm phim vào danh sách yêu thích
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| movieId   | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### 2. Remove Favorite (Xóa khỏi yêu thích)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/favorites/{movieId}`
- **Summary:** Xóa phim khỏi danh sách yêu thích
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| movieId   | string | ID của phim |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### 3. Get My Favorites (Lấy danh sách yêu thích)

- **HTTP Method:** `GET`
- **Endpoint:** `/favorites`
- **Summary:** Lấy danh sách phim yêu thích của tôi (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 20,
    "totalPages": 2,
    "data": [
      {
        "id": "string",
        "title": "string",
        "description": "string",
        "posterUrl": "string",
        "bannerUrl": "string",
        "type": "SERIES|MOVIE|ONA",
        "status": "ONGOING|COMPLETED|UPCOMING",
        "episodeCount": 0,
        "releaseDate": "2026-03-25",
        "views": 0,
        "rating": 0.0
      }
    ]
  }
}
```

---

## 📺 Watch History API

**Base URL:** `/history`
**Description:** Quản lý lịch sử xem phim của người dùng

### 1. Save Progress (Lưu tiến trình xem)

- **HTTP Method:** `POST`
- **Endpoint:** `/history`
- **Summary:** Lưu tiến trình xem phim
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Request Body

```json
{
  "episodeId": "string",
  "watchedDuration": 720,
  "totalDuration": 1440
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### 2. Get My History (Lấy lịch sử xem)

- **HTTP Method:** `GET`
- **Endpoint:** `/history`
- **Summary:** Lấy lịch sử xem phim của tôi (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 30,
    "totalPages": 3,
    "data": [
      {
        "id": "string",
        "episodeId": "string",
        "movieId": "string",
        "movieTitle": "string",
        "watchedDuration": 720,
        "totalDuration": 1440,
        "watchedAt": "2026-03-25T10:00:00Z"
      }
    ]
  }
}
```

---

## 🎭 Franchise Management API

**Base URL:** `/franchises`
**Description:** Quản lý chuỗi phim (Franchises)

### 1. Create Franchise (Tạo Franchise)

- **HTTP Method:** `POST`
- **Endpoint:** `/franchises`
- **Summary:** Tạo mới Franchise
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Request Body

```json
{
  "name": "string",
  "description": "string",
  "coverUrl": "string"
}
```

#### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "name": "string",
    "description": "string",
    "coverUrl": "string"
  }
}
```

---

### 2. Get All Franchises (Lấy danh sách Franchise)

- **HTTP Method:** `GET`
- **Endpoint:** `/franchises`
- **Summary:** Lấy danh sách Franchise (Phân trang)
- **Authentication:** ❌ No
- **Role Required:** None

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 15,
    "totalPages": 2,
    "data": [
      {
        "id": "string",
        "name": "string",
        "description": "string",
        "coverUrl": "string"
      }
    ]
  }
}
```

---

### 3. Get Franchise by ID (Lấy Franchise theo ID)

- **HTTP Method:** `GET`
- **Endpoint:** `/franchises/{id}`
- **Summary:** Lấy Franchise theo ID
- **Authentication:** ❌ No
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description      |
| --------- | ------ | ---------------- |
| id        | string | ID của Franchise |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "name": "string",
    "description": "string",
    "coverUrl": "string"
  }
}
```

---

### 4. Update Franchise (Cập nhật Franchise)

- **HTTP Method:** `PUT`
- **Endpoint:** `/franchises/{id}`
- **Summary:** Cập nhật Franchise
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description      |
| --------- | ------ | ---------------- |
| id        | string | ID của Franchise |

#### Request Body

```json
{
  "name": "string",
  "description": "string",
  "coverUrl": "string"
}
```

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "name": "string",
    "description": "string",
    "coverUrl": "string"
  }
}
```

---

### 5. Delete Franchise (Xóa Franchise)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/franchises/{id}`
- **Summary:** Xóa Franchise
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description      |
| --------- | ------ | ---------------- |
| id        | string | ID của Franchise |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 🔑 Role & Permission API

### Role API

**Base URL:** `/roles`
**Description:** Quản lý vai trò (Roles) (Admin)

#### 1. Create Role (Tạo vai trò)

- **HTTP Method:** `POST`
- **Endpoint:** `/roles`
- **Summary:** Tạo mới vai trò
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Request Body

```json
{
  "name": "string",
  "description": "string"
}
```

##### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "name": "string",
    "description": "string"
  }
}
```

---

#### 2. Get All Roles (Lấy tất cả vai trò)

- **HTTP Method:** `GET`
- **Endpoint:** `/roles`
- **Summary:** Lấy danh sách tất cả vai trò
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "name": "ADMIN",
      "description": "Administrator role"
    },
    {
      "name": "USER",
      "description": "Regular user role"
    }
  ]
}
```

---

#### 3. Delete Role (Xóa vai trò)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/roles/{role}`
- **Summary:** Xóa vai trò
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Path Parameters

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| role      | string | Tên vai trò |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### Permission API

**Base URL:** `/permissions`
**Description:** Quản lý quyền hạn (Permissions) (Admin)

#### 1. Create Permission (Tạo quyền hạn)

- **HTTP Method:** `POST`
- **Endpoint:** `/permissions`
- **Summary:** Tạo mới quyền hạn
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Request Body

```json
{
  "name": "string",
  "description": "string"
}
```

##### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "name": "string",
    "description": "string"
  }
}
```

---

#### 2. Get All Permissions (Lấy tất cả quyền hạn)

- **HTTP Method:** `GET`
- **Endpoint:** `/permissions`
- **Summary:** Lấy tất cả danh sách quyền hạn
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "name": "CREATE_MOVIE",
      "description": "Permission to create movies"
    },
    {
      "name": "DELETE_MOVIE",
      "description": "Permission to delete movies"
    }
  ]
}
```

---

#### 3. Delete Permission (Xóa quyền hạn)

- **HTTP Method:** `DELETE`
- **Endpoint:** `/permissions/{permission}`
- **Summary:** Xóa quyền hạn
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

##### Path Parameters

| Parameter  | Type   | Description   |
| ---------- | ------ | ------------- |
| permission | string | Tên quyền hạn |

##### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 📋 Report Management API

**Base URL:** `/reports`
**Description:** Quản lý báo cáo (Reports)

### 1. Create Report (Gửi báo cáo)

- **HTTP Method:** `POST`
- **Endpoint:** `/reports`
- **Summary:** Gửi báo cáo lỗi/vi phạm
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Request Body

```json
{
  "movieId": "string",
  "reason": "string",
  "description": "string"
}
```

#### Response (201 Created)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "string",
    "movieId": "string",
    "userId": "string",
    "reason": "string",
    "description": "string",
    "status": "PENDING",
    "createdAt": "2026-03-25T10:00:00Z"
  }
}
```

---

### 2. Get All Reports (Lấy danh sách báo cáo)

- **HTTP Method:** `GET`
- **Endpoint:** `/reports`
- **Summary:** Lấy danh sách tất cả báo cáo (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "data": [
      {
        "id": "string",
        "movieId": "string",
        "userId": "string",
        "reason": "string",
        "description": "string",
        "status": "PENDING",
        "createdAt": "2026-03-25T10:00:00Z"
      }
    ]
  }
}
```

---

### 3. Resolve Report (Đánh dấu báo cáo đã xử lý)

- **HTTP Method:** `PATCH`
- **Endpoint:** `/reports/{id}/resolve`
- **Summary:** Đánh dấu báo cáo đã xử lý
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Path Parameters

| Parameter | Type   | Description    |
| --------- | ------ | -------------- |
| id        | string | ID của báo cáo |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 🔔 Notification API

**Base URL:** `/notifications`
**Description:** Quản lý thông báo (Notifications)

### 1. Get My Notifications (Lấy thông báo của tôi)

- **HTTP Method:** `GET`
- **Endpoint:** `/notifications`
- **Summary:** Lấy danh sách thông báo của tôi (Phân trang)
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "data": [
      {
        "id": "string",
        "title": "string",
        "message": "string",
        "read": false,
        "createdAt": "2026-03-25T10:00:00Z"
      }
    ]
  }
}
```

---

### 2. Count Unread Notifications (Đếm thông báo chưa đọc)

- **HTTP Method:** `GET`
- **Endpoint:** `/notifications/unread-count`
- **Summary:** Đếm số lượng thông báo chưa đọc
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": 5
}
```

---

### 3. Mark Notification as Read (Đánh dấu đã đọc)

- **HTTP Method:** `PATCH`
- **Endpoint:** `/notifications/{id}/read`
- **Summary:** Đánh dấu thông báo đã đọc
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Path Parameters

| Parameter | Type   | Description      |
| --------- | ------ | ---------------- |
| id        | string | ID của thông báo |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

### 4. Mark All Notifications as Read (Đánh dấu tất cả đã đọc)

- **HTTP Method:** `PATCH`
- **Endpoint:** `/notifications/read-all`
- **Summary:** Đánh dấu tất cả thông báo là đã đọc
- **Authentication:** ✅ Yes
- **Role Required:** None

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": null
}
```

---

## 📊 Statistics API

**Base URL:** `/admin/stats`
**Description:** API thống kê cho Admin Dashboard

### Get Dashboard Statistics (Lấy thống kê tổng quan)

- **HTTP Method:** `GET`
- **Endpoint:** `/admin/stats/dashboard`
- **Summary:** Lấy dữ liệu thống kê tổng quan
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "totalUsers": 1000,
    "totalMovies": 500,
    "totalEpisodes": 5000,
    "totalViews": 50000,
    "activeUsers": 200,
    "newUsersThisMonth": 50,
    "totalComments": 1000,
    "totalReviews": 500,
    "totalReports": 10,
    "unresolvedReports": 3
  }
}
```

---

## 📤 Media Upload API

**Base URL:** `/media`
**Description:** Xử lý upload tệp tin (Images/Media)

### Upload File (Upload hình ảnh)

- **HTTP Method:** `POST`
- **Endpoint:** `/media/upload`
- **Summary:** Upload hình ảnh (Poster, Avatar, Banner...)
- **Authentication:** ❌ No (but recommended to be authenticated)
- **Role Required:** None
- **Content-Type:** `multipart/form-data`

#### Request Parameters

| Parameter | Type   | Required | Description                        |
| --------- | ------ | -------- | ---------------------------------- |
| file      | File   | Yes      | Tệp hình ảnh cần upload            |
| folder    | string | No       | Thư mục lưu trữ (default: general) |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": "http://example.com/uploads/general/image-uuid.jpg"
}
```

---

## � Audit Log API

**Base URL:** `/admin/audit-logs`
**Description:** Xem lịch sử hoạt động của hệ thống (Admin)

### Get Audit Logs (Xem danh sách lịch sử hoạt động)

- **HTTP Method:** `GET`
- **Endpoint:** `/admin/audit-logs`
- **Summary:** Xem danh sách lịch sử hoạt động - Chỉ dành cho Admin
- **Authentication:** ✅ Yes
- **Role Required:** ADMIN

#### Query Parameters

| Parameter | Type | Default | Description           |
| --------- | ---- | ------- | --------------------- |
| page      | int  | 1       | Số trang              |
| size      | int  | 10      | Số bản ghi trên trang |

#### Response (200 OK)

```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "currentPage": 1,
    "pageSize": 10,
    "totalElements": 150,
    "totalPages": 15,
    "data": [
      {
        "id": "string",
        "userId": "string",
        "userName": "string",
        "action": "string",
        "description": "string",
        "entityType": "string",
        "entityId": "string",
        "changes": "string",
        "ipAddress": "string",
        "userAgent": "string",
        "createdAt": "2026-03-25T10:00:00Z"
      }
    ]
  }
}
```

---

## �📌 Common Response Format

Tất cả API trả về response theo định dạng sau:

```json
{
  "code": 1000,
  "message": "Success",
  "result": {}
}
```

### Response Codes

| Code | Description     |
| ---- | --------------- |
| 1000 | Success         |
| 1001 | Invalid data    |
| 1002 | Unauthorized    |
| 1003 | Forbidden       |
| 1004 | Not found       |
| 1005 | Conflict        |
| 9999 | Undefined error |

---

## 🔐 Authentication

Sử dụng JWT Token trong header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📝 Notes

- Tất cả timestamp được định dạng theo ISO 8601 (UTC)
- Phân trang bắt đầu từ 1 (không phải 0)
- Các endpoint có `@PreAuthorize` yêu cầu role cụ thể
- Upload file hỗ trợ các định dạng: JPG, PNG, GIF, WebP
- Kích thước file tối đa: 10MB

---

**Generated on:** 25/03/2026
**Version:** 1.1
