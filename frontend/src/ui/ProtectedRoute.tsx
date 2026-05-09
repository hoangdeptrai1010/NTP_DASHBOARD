import { Navigate, useLocation } from 'react-router-dom'
import { authStore } from '../stores/authStore'
import type { Role } from '../types'

interface ProtectedRouteProps {
  allowedRoles: Role[]
  children: React.ReactNode
}

export function ProtectedRoute({ allowedRoles, children }: ProtectedRouteProps) {
  const location = useLocation()
  const isBootstrapped = authStore((state) => state.isBootstrapped)
  const user = authStore((state) => state.user)

  if (!isBootstrapped) {
    return (
      <div className="centered-state">
        <span className="eyebrow">Đang xác thực</span>
        <h1>Đang khôi phục phiên đăng nhập...</h1>
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  if (!allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" replace />
  }

  return <>{children}</>
}
