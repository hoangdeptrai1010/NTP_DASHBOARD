import { useState } from 'react'
import type { FormEvent } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { login } from '../services/authService'
import { authStore } from '../stores/authStore'

export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const isBootstrapped = authStore((state) => state.isBootstrapped)
  const setSession = authStore((state) => state.setSession)
  const user = authStore((state) => state.user)

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  if (isBootstrapped && user) {
    const redirectTo = location.state?.from?.pathname ?? '/dashboard'
    return <Navigate replace to={redirectTo} />
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setSubmitting(true)
    setError(null)

    try {
      const response = await login({ username, password })
      setSession(response.accessToken, response.user)
      const redirectTo = location.state?.from?.pathname ?? '/dashboard'
      navigate(redirectTo, { replace: true })
    } catch {
      setError('Đăng nhập thất bại. Vui lòng dùng tài khoản thật trong Oracle.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="login-page">
      <section className="login-brand">
        <span className="eyebrow">Hệ thống quản trị</span>
        <h1>Dashboard bệnh viện</h1>
      </section>

      <section className="login-card">
        <form className="login-form" onSubmit={handleSubmit}>
          <label>
            Username
            <input
              autoComplete="username"
              onChange={(event) => setUsername(event.target.value)}
              value={username}
            />
          </label>
          <label>
            Password
            <input
              autoComplete="current-password"
              onChange={(event) => setPassword(event.target.value)}
              type="password"
              value={password}
            />
          </label>
          <button className="primary-button" disabled={submitting} type="submit">
            {submitting ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
          {error ? <p className="form-error">{error}</p> : null}
        </form>

        <div className="demo-note">
          <p>Dashboard bệnh viện</p>
        </div>
      </section>
    </div>
  )
}
