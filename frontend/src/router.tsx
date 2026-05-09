import { Navigate, Route, Routes } from 'react-router-dom'
import { AppShell } from './ui/AppShell'
import { ProtectedRoute } from './ui/ProtectedRoute'
import { DashboardPage } from './views/DashboardPage'
import { LoginPage } from './views/LoginPage'
import { ReportPage } from './views/ReportPage'
import { UnauthorizedPage } from './views/UnauthorizedPage'

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute allowedRoles={['DIRECTOR', 'DEPARTMENT_HEAD', 'DOCTOR']}>
            <AppShell>
              <DashboardPage />
            </AppShell>
          </ProtectedRoute>
        }
      />
      <Route
        path="/reports"
        element={
          <ProtectedRoute allowedRoles={['DIRECTOR', 'DEPARTMENT_HEAD', 'DOCTOR']}>
            <AppShell>
              <ReportPage />
            </AppShell>
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}
