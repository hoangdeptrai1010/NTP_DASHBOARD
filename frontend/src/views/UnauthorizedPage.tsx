import { Link } from 'react-router-dom'

export function UnauthorizedPage() {
  return (
    <div className="centered-state">
      <span className="eyebrow">403</span>
      <h1>Bạn không có quyền truy cập khu vực này.</h1>
      <Link className="primary-button inline-button" to="/dashboard">
        Về dashboard
      </Link>
    </div>
  )
}
