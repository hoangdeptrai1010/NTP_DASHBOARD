# Frontend Components Overview

This document provides a comprehensive overview of the frontend architecture and components in the NTP Hospital Dashboard project. The frontend is built using **React, TypeScript, and Vite**, with state management handled by **Zustand** (for auth) and **React Query** (for data fetching). It utilizes **React Router** for navigation and **Axios** for API communication.

## 📁 Directory Structure Overview
- `src/views/`: Page-level components.
- `src/ui/`: Shared UI components, layout elements, and specific dashboard widgets.
- `src/ui/charts/`: Data visualization components.
- `src/services/`: API client setup and backend communication logic.
- `src/stores/`: Global state management.
- `src/hooks/`: Custom React hooks.
- `src/styles/`: Global CSS and design tokens.

---

## 🧩 Components Detail

### 1. Views (`src/views/`)
These are the main pages mapped to specific routes.

*   **`DashboardPage.tsx`**: The core application view. It orchestrates the dashboard by:
    *   Providing global filters for Time Period (Month, Quarter, Year) and Department.
    *   Fetching dashboard snapshot data (KPIs, Revenue) via React Query.
    *   Rendering sub-components like `KpiCard` and `RevenuePanel`.
*   **`LoginPage.tsx`**: Handles user authentication, collecting credentials and calling the auth service.
*   **`ReportPage.tsx`**: A dedicated page for analytics and report data exports.
*   **`UnauthorizedPage.tsx`**: A fallback page displayed when a user attempts to access a route restricted by their Role-Based Access Control (RBAC) level.

### 2. Layout & Routing Components (`src/ui/`)
Components that manage the overall structure and access of the application.

*   **`AppShell.tsx`**: The main layout wrapper for authenticated users. It contains the application's persistent Sidebar (with navigation links) and handles the logout action.
*   **`ProtectedRoute.tsx`**: A routing wrapper that checks the current user's role against an `allowedRoles` array (e.g., `['DIRECTOR', 'DEPARTMENT_HEAD', 'DOCTOR']`). If unauthorized, redirects to the Unauthorized page.
*   **`AuthBootstrap.tsx`**: A utility component that runs on application startup to verify existing sessions (checking refresh tokens/access tokens) before rendering the app.

### 3. Dashboard Widgets & Panels (`src/ui/`)
Specific components that display data on the Dashboard.

*   **`KpiCard.tsx`**: Displays individual Key Performance Indicators (e.g., Total Revenue, Patient Count).
*   **`RevenuePanel.tsx`**: The primary chart container for revenue visualization. Integrates the chart switcher and the actual chart component.

### 4. Charts (`src/ui/charts/`)
Custom data visualization components built with raw SVGs.

*   **`TrendChart.tsx`**: A versatile, custom SVG-based chart component capable of rendering `bar`, `line`, and `area` charts. It dynamically scales based on data and handles tooltips/labels.
*   **`ChartTypeSwitcher.tsx`**: A UI control (toggle buttons) allowing users to switch the `TrendChart` view between Bar, Line, and Area modes.
*   **`ChartCard.tsx`**: A styling wrapper to enclose charts within a standard dashboard card layout.

---

## ⚙️ Services & State Management

### State Management (`src/stores/`, `src/hooks/`)
*   **`authStore.ts` (Zustand)**: Manages global authentication state including `accessToken`, `user` profile data, and session status (`isBootstrapped`). Includes functions for login, logout, and token setting.
*   **`useChart.ts`**: A custom hook for managing local state related to chart rendering (like tracking the currently selected chart type).

### API Services (`src/services/`)
*   **`api.ts`**: The core Axios instance configuration. It sets up base URLs and crucial **interceptors**:
    *   *Request Interceptor*: Automatically attaches the `Authorization: Bearer <token>` header to requests.
    *   *Response Interceptor*: Catches `401 Unauthorized` errors and automatically attempts a silent token refresh using the `refresh_token` cookie.
*   **`authService.ts`**: Contains API calls for user authentication (`login`, `logout`, `refresh`).
*   **`dashboardService.ts`**: Contains API calls for fetching application data (`fetchSnapshot`, `fetchDepartments`).

## 🛣️ Routing (`src/router.tsx`)
*   **`AppRouter`**: Defines the application routes. Uses `react-router-dom`. Routes like `/dashboard` and `/reports` are wrapped in `<ProtectedRoute>` to enforce Role-Based Access Control, while `/login` is public.
