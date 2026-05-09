# 🏥 Hospital Dashboard — Hệ thống Dashboard quản trị bệnh viện

Hệ thống theo dõi doanh thu, lưu lượng bệnh nhân, cơ cấu BHYT và hiệu suất vận hành bệnh viện theo thời gian thực — kết nối trực tiếp với Oracle Database.

---

## 📸 Tổng quan

| Trang | Mô tả |
|-------|-------|
| **Login** | Đăng nhập bằng tài khoản nội bộ, phân quyền theo vai trò |
| **Dashboard tổng quan** | 4 KPI chính + biểu đồ doanh thu theo thời gian + Top khoa doanh thu cao + Tỉ lệ Dịch vụ/Thuốc |
| **Phân tích BHYT** | 2 biểu đồ Donut (Nội/Ngoại trú, BHYT/Tự trả) + Top 10 khoa so sánh BHYT vs Tự trả |

---

## 🏗️ Kiến trúc

```
┌──────────────────────────────────────────────┐
│           React Frontend (Vite)              │
│  Login · Dashboard · Phân tích · Charts SVG  │
│  Port: 5173                                  │
└──────────────────┬───────────────────────────┘
                   │ HTTP + JWT Bearer
┌──────────────────▼───────────────────────────┐
│          Spring Boot 3 Backend               │
│  Security · Auth · Dashboard API · Analysis  │
│  Port: 8081                                  │
└──────────────────┬───────────────────────────┘
                   │ JDBC (ojdbc11)
┌──────────────────▼───────────────────────────┐
│            Oracle Database 21c               │
│  21 bảng nghiệp vụ + auth + phân quyền      │
└──────────────────────────────────────────────┘
```

---

## ⚙️ Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản | Vai trò |
|------------|-----------|---------|
| Java | 21 | Ngôn ngữ chính |
| Spring Boot | 3.5 | Framework backend |
| Spring Security | 6.x | Xác thực & phân quyền |
| JJWT | 0.12.7 | Tạo & xác minh JWT |
| Oracle JDBC (ojdbc11) | — | Kết nối Oracle DB |
| Lombok | — | Giảm boilerplate code |
| Maven Wrapper | — | Build tool |

### Frontend
| Công nghệ | Phiên bản | Vai trò |
|------------|-----------|---------|
| React | 18.3 | UI framework |
| TypeScript | 5.x | Type safety |
| Vite | 5.x | Dev server & bundler |
| React Router | 6.x | Routing & navigation |
| TanStack React Query | 5.x | Server state management |
| Zustand | 5.x | Client state (auth) |
| Axios | 1.x | HTTP client |
| SVG thuần | — | Biểu đồ (Bar, Line, Donut, Stacked Bar) |

### Database
| Công nghệ | Phiên bản |
|------------|-----------|
| Oracle Database | 21c Enterprise Edition |

---

## 📂 Cấu trúc thư mục

```
dashboard_vibecode/
├── .gitignore                            # Git ignore rules
├── AGENTS.md                             # Tài liệu kiến trúc tổng thể
├── DESIGN-lovable.md                     # Tài liệu thiết kế Lovable design system
├── UIDESIGN.MD                           # Tài liệu thiết kế giao diện chi tiết
├── frontend.md                           # Tài liệu frontend overview
├── dashboard.sql                         # Schema Oracle (CREATE TABLE + seed roles)
├── seed_data.sql                         # Dữ liệu mẫu 2016–2026 (~4700 lượt khám)
│
├── docs/                                 # Tài liệu dự án
│   └── project-overview.md              # Tổng quan dự án
│
├── backend/                              # Spring Boot API
│   ├── .env.example                     # Biến môi trường mẫu
│   ├── .gitattributes                   # Git attributes
│   ├── .gitignore                       # Git ignore (backend)
│   ├── pom.xml                          # Maven dependencies & build config
│   ├── mvnw / mvnw.cmd                 # Maven Wrapper (Linux/Windows)
│   ├── HELP.md                          # Spring Boot help
│   ├── .mvn/                            # Maven Wrapper config
│   └── src/main/
│       ├── resources/
│       │   └── application.yml          # Cấu hình DB, JWT, server port
│       └── java/com/hospital/dashboard/
│           ├── DashboardApplication.java    # Main Spring Boot entry point
│           ├── auth/                        # Xác thực & phân quyền
│           │   ├── AuthController.java      # POST /api/auth/login, /refresh
│           │   ├── AuthService.java         # Xác thực username/password
│           │   ├── JwtService.java          # Tạo & validate JWT token
│           │   ├── JwtAuthenticationFilter.java # Filter xác thực JWT trên mỗi request
│           │   ├── RefreshTokenService.java # Quản lý refresh token (cookie)
│           │   ├── AppUser.java             # Entity user
│           │   ├── AppRole.java             # Entity role
│           │   ├── AppUserRepository.java   # JPA repository user
│           │   ├── AppRoleRepository.java   # JPA repository role
│           │   ├── AuthUser.java            # UserDetails implementation
│           │   ├── UserProfile.java         # DTO thông tin user trả về client
│           │   ├── LoginRequest.java        # DTO request đăng nhập
│           │   ├── LoginResponse.java       # DTO response đăng nhập
│           │   ├── RefreshResponse.java     # DTO response refresh token
│           │   ├── AuthPayload.java         # DTO payload JWT
│           │   └── AuthException.java       # Custom exception xác thực
│           ├── config/                      # Cấu hình ứng dụng
│           │   ├── SecurityConfig.java      # Spring Security filter chain
│           │   ├── DataInitializer.java     # Khởi tạo roles & admin user
│           │   └── AppProperties.java       # Custom config properties
│           ├── dashboard/                   # API dashboard & phân tích
│           │   ├── DashboardController.java     # GET /snapshot, /revenue, /analysis
│           │   ├── DashboardService.java        # Business logic + Oracle queries
│           │   ├── DashboardSnapshotResponse.java # DTO snapshot tổng quan
│           │   ├── AnalysisResponse.java        # DTO cho tab phân tích
│           │   ├── RevenueResponse.java         # DTO doanh thu theo kỳ
│           │   ├── RevenuePointResponse.java    # DTO điểm dữ liệu doanh thu
│           │   ├── KpiCardResponse.java         # DTO card KPI
│           │   ├── RatioItem.java               # DTO tỉ lệ (Donut chart)
│           │   └── DeptCompareItem.java         # DTO so sánh khoa
│           ├── department/                  # API danh sách khoa phòng
│           │   ├── DepartmentController.java    # GET /api/departments
│           │   ├── DepartmentRepository.java    # JPA repository khoa
│           │   ├── Department.java              # Entity khoa phòng
│           │   └── DepartmentResponse.java      # DTO khoa phòng
│           ├── layout/                      # Lưu/tải layout preferences
│           │   ├── LayoutController.java        # GET/PUT /api/layout
│           │   ├── LayoutService.java           # Business logic layout
│           │   ├── LayoutPreferenceEntity.java  # Entity layout preference
│           │   ├── LayoutPreferenceRepository.java # JPA repository layout
│           │   └── LayoutPreference.java        # DTO layout preference
│           └── common/                      # Xử lý lỗi & tiện ích chung
│               ├── GlobalExceptionHandler.java  # Bắt & format lỗi toàn cục
│               └── ApiError.java                # DTO response lỗi
│
├── frontend/                             # React + Vite + TypeScript
│   ├── .gitignore                       # Git ignore (frontend)
│   ├── index.html                       # HTML entry point
│   ├── package.json                     # Dependencies & scripts
│   ├── package-lock.json                # Lock file
│   ├── vite.config.ts                   # Vite config (proxy, build)
│   ├── tsconfig.json                    # TypeScript config gốc
│   ├── tsconfig.app.json                # TypeScript config cho app
│   ├── tsconfig.node.json               # TypeScript config cho Node
│   ├── eslint.config.js                 # ESLint config
│   ├── public/                          # Static assets
│   │   ├── favicon.svg                  # Favicon
│   │   └── icons.svg                    # SVG icon sprite
│   └── src/
│       ├── main.tsx                     # React entry point
│       ├── router.tsx                   # React Router config
│       ├── types.ts                     # TypeScript interfaces
│       ├── styles.css                   # Toàn bộ CSS (Lovable design)
│       ├── vite-env.d.ts                # Vite type declarations
│       ├── assets/                      # Static assets (images)
│       │   ├── hero.png                 # Ảnh hero login page
│       │   ├── react.svg                # React logo
│       │   └── vite.svg                 # Vite logo
│       ├── views/                       # Trang chính
│       │   ├── LoginPage.tsx            # Trang đăng nhập
│       │   ├── DashboardPage.tsx        # Trang tổng quan chính
│       │   ├── ReportPage.tsx           # Trang phân tích BHYT & Nội/Ngoại
│       │   └── UnauthorizedPage.tsx     # Trang 403
│       ├── ui/                          # UI Components
│       │   ├── AppShell.tsx             # Layout: Sidebar + Content
│       │   ├── AuthBootstrap.tsx        # Khởi tạo auth state khi app load
│       │   ├── KpiCard.tsx              # Card chỉ số KPI
│       │   ├── RevenuePanel.tsx         # Panel biểu đồ doanh thu
│       │   ├── ProtectedRoute.tsx       # Route guard theo role
│       │   └── charts/                  # SVG Chart components
│       │       ├── TrendChart.tsx       # Biểu đồ Bar/Line (SVG)
│       │       ├── DonutChart.tsx       # Biểu đồ Donut (SVG)
│       │       ├── StackedBarChart.tsx  # Biểu đồ thanh xếp chồng
│       │       ├── ChartCard.tsx        # Wrapper card cho chart
│       │       └── ChartTypeSwitcher.tsx # Nút chuyển loại chart
│       ├── services/                    # API layer
│       │   ├── api.ts                   # Axios instance + JWT interceptor
│       │   ├── authService.ts           # Login/refresh API calls
│       │   └── dashboardService.ts      # Dashboard & Analysis API calls
│       ├── stores/                      # State management
│       │   └── authStore.ts             # Zustand: user, token, session
│       ├── hooks/                       # Custom React hooks
│       │   └── useChart.ts              # Hook quản lý loại chart
│       └── styles/                      # CSS modules bổ sung
│           └── chart-tokens.css         # CSS variables cho charts
```

---

## 🚀 Hướng dẫn cài đặt & chạy

### Yêu cầu hệ thống

- **Java** 21+
- **Node.js** 18+
- **Oracle Database** 21c (hoặc 19c+)
- **Maven** (sử dụng Maven Wrapper đi kèm)

### Bước 1: Khởi tạo Database

```bash
# Kết nối vào Oracle với user có quyền tạo schema
sqlplus sys/password@localhost:1521/orclpdb as sysdba

# Tạo user dashboard (nếu chưa có)
CREATE USER dashboard IDENTIFIED BY dashboard;
GRANT CONNECT, RESOURCE, UNLIMITED TABLESPACE TO dashboard;
```

```bash
# Chạy schema
cd dashboard_vibecode
sqlplus dashboard/dashboard@localhost:1521/orclpdb @dashboard.sql

# Nạp dữ liệu mẫu (500 bệnh nhân, ~4700 lượt khám từ 2016)
sqlplus dashboard/dashboard@localhost:1521/orclpdb @seed_data.sql
```

### Bước 2: Chạy Backend

```bash
cd backend
.\mvnw.cmd spring-boot:run       # Windows
./mvnw spring-boot:run           # Linux/Mac
```

Backend sẽ chạy tại `http://localhost:8081`.

> **Cấu hình kết nối DB** nằm trong `backend/src/main/resources/application.yml`.  
> Mặc định: `jdbc:oracle:thin:@//localhost:1521/orclpdb` với user `dashboard/dashboard`.

### Bước 3: Chạy Frontend

```bash
cd frontend
npm install        # Lần đầu
npm run dev
```

Frontend sẽ chạy tại `http://localhost:5173`.

### Bước 4: Đăng nhập

Truy cập `http://localhost:5173` → Đăng nhập với:

| Tài khoản | Mật khẩu | Vai trò |
|-----------|----------|---------|
| `admin` | `admin` | Giám đốc (xem toàn bộ) |

---

## 📊 API Endpoints

| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| `POST` | `/api/auth/login` | Đăng nhập, trả JWT | ❌ |
| `POST` | `/api/auth/refresh` | Làm mới access token | Cookie |
| `GET` | `/api/dashboard/snapshot` | KPI + Doanh thu + Tỉ lệ DV/Thuốc + Top khoa | ✅ JWT |
| `GET` | `/api/dashboard/revenue` | Doanh thu chi tiết theo kỳ | ✅ JWT |
| `GET` | `/api/dashboard/analysis` | Phân tích BHYT: Nội/Ngoại, BHYT/Tự trả, Top 10 | ✅ JWT |
| `GET` | `/api/departments` | Danh sách khoa phòng | ✅ JWT |

**Query parameters:**
- `period` — `week` (12 tháng) | `quarter` (8 quý) | `year` (tất cả năm)
- `departmentId` — Lọc theo khoa (chỉ Giám đốc mới chọn được)

---

## 🎨 Design System — Lovable

| Token | Giá trị | Dùng cho |
|-------|---------|----------|
| Cream | `#f7f4ed` | Nền trang |
| Charcoal | `#1c1c1c` | Sidebar, text chính |
| Emerald | `#1d9e75` | KPI tích cực |
| Blue | `#378add` | Biểu đồ, accent |
| Orange | `#ef9f27` | Cảnh báo |
| Violet | `#7b68c8` | Kỳ hiển thị |
| Border radius | `22px` | Panel, card |
| Font | Segoe UI, Inter | Toàn hệ thống |

---

## 🔐 Phân quyền (RBAC)

| Vai trò | Dashboard tổng quan | Chọn khoa | Phân tích BHYT | Xem doanh thu |
|---------|:-------------------:|:---------:|:--------------:|:-------------:|
| **Giám đốc** | ✅ | ✅ Tất cả khoa | ✅ | ✅ |
| **Trưởng khoa** | ✅ | ❌ Chỉ khoa mình | ✅ | ✅ |
| **Bác sĩ** | ✅ | ❌ Chỉ khoa mình | ✅ | ❌ |

---

## 🗃️ Cơ sở dữ liệu Oracle

### Sơ đồ quan hệ chính

```
DMKHOAPHONGBV (10 khoa)
    ├── VIENPHI (viện phí chính)
    │       ├── VIENPHICT (chi tiết dịch vụ)
    │       └── VIENPHICTTHUOC (chi tiết thuốc)
    └── APP_USERS (tài khoản)

DMNHOMVIENPHI → DMLOAIVIENPHI → DMGIAVIENPHI (danh mục giá dịch vụ)
DMNHOMKHO → DMKHO (kho thuốc/vật tư)
DMNHOMKHO → M_DMBD (biệt dược)
APP_ROLES → APP_USERS → BACSI / TRUONGKHOA / GIAMDOC
```

### Dữ liệu mẫu (seed_data.sql)

| Bảng | Số lượng | Ghi chú |
|------|----------|---------|
| BENHNHAN | 500 | Bệnh nhân từ 1950–2008 |
| VIENPHI | ~4,700 | 2016–2026, tăng dần theo năm |
| VIENPHICT | ~9,500 | 1–3 dịch vụ/lượt |
| VIENPHICTTHUOC | ~7,000 | 1–2 thuốc/lượt |

Phân bổ: **40% nội trú / 60% ngoại trú**, **65% có BHYT / 35% tự trả**.

---

## 🛠️ Phát triển tiếp

- [ ] Redis cache cho dashboard queries (TTL 5 phút)
- [ ] Audit log ghi lại mọi truy cập API
- [ ] Export báo cáo PDF/Excel
- [ ] Dashboard layout builder (kéo thả widget)
- [ ] Quản lý tài khoản (CRUD users)
- [ ] SSO qua Google Workspace
- [ ] Dark mode
- [ ] Responsive mobile

---

## 📝 Ghi chú kỹ thuật

- **JWT**: Access token 15 phút, Refresh token 7 ngày (HttpOnly cookie)
- **Encoding**: Backend ép UTF-8 qua `application.yml` + JVM flag `-Dfile.encoding=UTF8`
- **Biểu đồ**: Toàn bộ vẽ bằng SVG thuần (không dùng thư viện chart bên thứ 3)
- **Proxy**: Vite dev server proxy `/api` → `http://localhost:8081`
- **Password**: BCrypt cost factor 12

---

> **Liên hệ**: Dự án được phát triển cho hệ thống quản trị bệnh viện NTP.