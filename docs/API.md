# Tài liệu API & Workflow Kiến trúc Dự án

Tài liệu này mô tả chi tiết chức năng của từng file trong hệ thống và luồng dữ liệu (workflow) tổng thể giữa Frontend (React) và Backend (Spring Boot).

---

## 🔄 Tổng quan Workflow (Luồng dữ liệu)

Khi người dùng tương tác với hệ thống, luồng dữ liệu cơ bản diễn ra như sau:

1. **Người dùng (Browser):** Tương tác với các View (`views/`) hoặc Component (`ui/`).
2. **Frontend Services:** Component gọi các hàm trong `frontend/src/services/` (dùng thư viện Axios). Axios sẽ đính kèm JWT Token vào Header (`api.ts`).
3. **Frontend State:** Dữ liệu nhận về được lưu vào Local State (Zustand trong `stores/` hoặc Hooks).
4. **Backend Controller:** Spring Boot nhận Request tại các Controller (`*Controller.java`). Tại đây, `JwtAuthenticationFilter` sẽ chặn lại để kiểm tra tính hợp lệ của Token trước khi cho đi tiếp.
5. **Backend Service:** Controller gọi các Service (`*Service.java`). Service chứa business logic, xử lý dữ liệu và tính toán.
6. **Backend Repository & DB:** Service gọi các Repository (Spring Data JPA) để query thẳng xuống Oracle Database.
7. **Response:** Dữ liệu từ DB được map thành các đối tượng DTO (Data Transfer Object) và trả về dạng JSON cho Frontend render lên các biểu đồ (SVG Charts).

---

## 🖥️ BACKEND: Cấu trúc & Chức năng các file

Thư mục: `backend/src/main/java/com/hospital/dashboard/`

### 1. Core & Cấu hình (`/config`, `/common`, Root)
- `DashboardApplication.java`: Điểm entry point khởi động toàn bộ ứng dụng Spring Boot.
- `config/SecurityConfig.java`: Cấu hình Spring Security, định nghĩa các API nào cần bảo vệ (cần JWT), cấu hình CORS.
- `config/DataInitializer.java`: Chạy một lần khi khởi động để seed dữ liệu mẫu (Roles: Admin, Trưởng khoa, Bác sĩ...) nếu DB chưa có.
- `config/AppProperties.java`: Map các cấu hình custom từ file `application.yml` vào code Java.
- `common/GlobalExceptionHandler.java`: Bắt mọi lỗi (Exception) trong ứng dụng và format thành JSON chuẩn trả về cho client.
- `common/ApiError.java`: DTO định dạng cấu trúc báo lỗi thống nhất.

### 2. Xác thực & Phân quyền (`/auth`)
- **Controllers & Services:**
  - `AuthController.java`: Chứa API `/api/auth/login` và `/api/auth/refresh`. Nhận request đăng nhập từ Frontend.
  - `AuthService.java`: Nhận username/password, kiểm tra với DB, nếu đúng thì tạo token.
  - `JwtService.java`: Chịu trách nhiệm mã hóa và giải mã chuỗi JWT.
  - `RefreshTokenService.java`: Quản lý logic tạo và cấp lại Refresh Token lưu trong HttpOnly Cookie.
- **Security Filter:**
  - `JwtAuthenticationFilter.java`: Filter đứng trước mọi request, tách chuỗi `Bearer {token}` từ header, xác thực bằng `JwtService` và set Context cho Spring Security.
- **Entities & Repositories (DB):**
  - `AppUser.java`, `AppRole.java`: Các Entity map với bảng Users và Roles trong Oracle.
  - `AppUserRepository.java`, `AppRoleRepository.java`: Interface tương tác với DB để lấy User/Role.
- **DTOs:**
  - `AuthUser.java`, `UserProfile.java`, `LoginRequest.java`, `LoginResponse.java`, `RefreshResponse.java`, `AuthPayload.java`: Các class trung gian để trao đổi dữ liệu (Request/Response) mà không làm lộ Entity gốc.

### 3. Nghiệp vụ Dashboard (`/dashboard`)
- `DashboardController.java`: Cung cấp các API chính `/snapshot`, `/revenue`, `/analysis` để Frontend vẽ biểu đồ.
- `DashboardService.java`: Chứa các query SQL/JPQL phức tạp (Group by theo thời gian, tính tỉ lệ, Join các bảng Viện Phí, Dịch Vụ, Thuốc) trên Oracle.
- **DTOs (`*Response.java`, `*Item.java`):**
  - `DashboardSnapshotResponse.java`, `KpiCardResponse.java`: Trả về dữ liệu cho 4 thẻ KPI trên cùng.
  - `RevenueResponse.java`, `RevenuePointResponse.java`: Trả về mảng dữ liệu doanh thu theo dòng thời gian (Tháng/Quý/Năm).
  - `AnalysisResponse.java`, `RatioItem.java`, `DeptCompareItem.java`: Dữ liệu phân tích tỷ lệ BHYT/Tự trả, Nội/Ngoại trú dạng phần trăm.

### 4. Danh mục Khoa phòng (`/department`)
- `DepartmentController.java` & `DepartmentService.java`: API cấp danh sách các khoa phòng trong bệnh viện để làm bộ lọc Filter trên giao diện.
- `Department.java`, `DepartmentRepository.java`: Entity và Repo truy vấn bảng `DMKHOAPHONGBV`.

### 5. Layout Preferences (`/layout`)
- `LayoutController.java`, `LayoutService.java`: API GET/PUT để lưu lại vị trí, trạng thái của các biểu đồ (widget) do user kéo thả hoặc tùy chỉnh.
- `LayoutPreferenceEntity.java`, `LayoutPreferenceRepository.java`: Lưu cấu hình này vào Database tương ứng với từng `userId`.

---

## 🎨 FRONTEND: Cấu trúc & Chức năng các file

Thư mục: `frontend/src/`

### 1. Khởi động & Routing (Root & `/views`)
- `main.tsx`: Entry point của React, khởi tạo React DOM, bọc ứng dụng trong các Provider (Router, React Query, Auth).
- `router.tsx`: Định nghĩa các tuyến đường (URL). Trỏ URL nào vào View nào (ví dụ: `/` vào Dashboard, `/login` vào LoginPage).
- `views/LoginPage.tsx`: Trang đăng nhập.
- `views/DashboardPage.tsx`: Trang chính, gọi API `/snapshot` và `/revenue` để render KPI và biểu đồ doanh thu.
- `views/ReportPage.tsx`: Trang phân tích chuyên sâu (BHYT, Nội/Ngoại trú).
- `views/UnauthorizedPage.tsx`: Trang báo lỗi 403 khi user (ví dụ Bác sĩ) cố gắng vào các trang dành riêng cho Giám đốc.

### 2. Quản lý trạng thái & API (`/stores`, `/hooks`, `/services`)
- `stores/authStore.ts`: Dùng thư viện Zustand để lưu trạng thái đăng nhập (Thông tin User, JWT Token) ở global scope. Bất kỳ component nào cũng có thể lấy ra dùng.
- `services/api.ts`: Cấu hình thư viện Axios. Tại đây có một `Interceptor` để tự động nhét token vào header `Authorization: Bearer ...` cho mọi request.
- `services/authService.ts`: Chứa hàm gọi API `/login` và logic lưu token/cookie.
- `services/dashboardService.ts`: Chứa các hàm gọi API `/snapshot`, `/analysis`, v.v. để lấy dữ liệu vẽ chart.
- `hooks/useChart.ts`: Custom hook xử lý logic chuyển đổi giữa các loại biểu đồ (từ Bar sang Line...).

### 3. UI Components (`/ui`)
- `AppShell.tsx`: Chứa layout khung của ứng dụng (Thanh Sidebar bên trái, Header, và khu vực nội dung chính).
- `AuthBootstrap.tsx`: Component chạy ngầm lúc mới load trang để check xem có token cũ trong localStorage/cookie không, tự động đăng nhập lại nếu có.
- `ProtectedRoute.tsx`: Component bảo vệ Route. Nếu chưa đăng nhập sẽ đá văng ra `/login`. Nếu đăng nhập rồi nhưng không đủ quyền (role) sẽ đá ra trang Unauthorized.
- `KpiCard.tsx`, `RevenuePanel.tsx`: Các khối UI cụ thể hiển thị thông số.

### 4. Hệ thống Biểu đồ SVG thuần (`/ui/charts`)
Dự án sử dụng SVG thuần thay vì thư viện ngoài để có hiệu năng cao và thiết kế đồng nhất Lovable:
- `TrendChart.tsx`: Biểu đồ dạng Bar/Line thể hiện xu hướng theo thời gian (Doanh thu).
- `DonutChart.tsx`: Biểu đồ hình khuyên (vòng tròn) chia tỷ lệ %.
- `StackedBarChart.tsx`: Biểu đồ cột xếp chồng.
- `ChartCard.tsx`: Khung thẻ card bọc bên ngoài mỗi biểu đồ.
- `ChartTypeSwitcher.tsx`: Nút bấm góc trên để đổi nhanh từ dạng Cột sang dạng Đường.

### 5. Styles & Types (`/styles`, `/assets`)
- `styles.css`: Chứa toàn bộ CSS Global của ứng dụng (Màu sắc Cream, Charcoal, Emerald... theo chuẩn Lovable).
- `styles/chart-tokens.css`: Chứa biến CSS riêng để style màu, trục, grid cho các file SVG Chart.
- `types.ts`: Định nghĩa toàn bộ các Interface Typescript (như `User`, `Snapshot`, `RevenueData`) để đồng bộ kiểu dữ liệu chặt chẽ với Backend DTO.
