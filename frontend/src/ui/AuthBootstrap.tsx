import { useEffect } from 'react'
import { refreshSession } from '../services/authService'
import { authStore } from '../stores/authStore'

let bootstrapPromise: Promise<void> | null = null

function bootstrapSession() {
  const { setSession, finishBootstrap, logout } = authStore.getState()

  if (!bootstrapPromise) {
    bootstrapPromise = (async () => {
      try {
        const response = await refreshSession()
        setSession(response.accessToken, response.user)
      } catch {
        logout()
      } finally {
        finishBootstrap()
      }
    })()
  }

  return bootstrapPromise
}

export function AuthBootstrap({ children }: { children: React.ReactNode }) {
  const isBootstrapped = authStore((state) => state.isBootstrapped)

  useEffect(() => {
    if (!isBootstrapped) {
      void bootstrapSession()
    }
  }, [isBootstrapped])

  if (!isBootstrapped) {
    return (
      <div className="centered-state">
        <span className="eyebrow">Đang xác thực</span>
        <h1>Đang khôi phục phiên đăng nhập...</h1>
        <p>Hệ thống đang kiểm tra refresh token trước khi hiển thị dashboard.</p>
      </div>
    )
  }

  return <>{children}</>
}
