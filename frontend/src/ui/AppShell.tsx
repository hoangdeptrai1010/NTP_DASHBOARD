import type { ReactNode } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { authStore, roleLabels } from '../stores/authStore'
import { logout } from '../services/authService'

function GridIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="16" viewBox="0 0 16 16" width="16">
      <rect fill="currentColor" height="5" rx="1.2" width="5" x="1.5" y="1.5" />
      <rect fill="currentColor" height="5" rx="1.2" opacity="0.72" width="5" x="9.5" y="1.5" />
      <rect fill="currentColor" height="5" rx="1.2" opacity="0.72" width="5" x="1.5" y="9.5" />
      <rect fill="currentColor" height="5" rx="1.2" width="5" x="9.5" y="9.5" />
    </svg>
  )
}

function ChartIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="16" viewBox="0 0 16 16" width="16">
      <path
        d="M2.5 11.5L5.5 7.5L8 9.5L11 4.5L13.5 6"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.6"
      />
      <path d="M2 13.5H14" opacity="0.45" stroke="currentColor" strokeLinecap="round" />
    </svg>
  )
}

const navSections = [
  {
    title: 'TỔNG QUAN',
    items: [
      {
        href: '/dashboard',
        icon: <GridIcon />,
        label: 'Dashboard',
        meta: 'KPI và tín hiệu chính',
      },
    ],
  },
  {
    title: 'PHÂN TÍCH',
    items: [
      {
        href: '/reports',
        icon: <ChartIcon />,
        label: 'BHYT & Nội/ngoại trú',
        meta: 'Phân tích cơ cấu doanh thu',
      },
    ],
  },
]

function getInitials(name?: string) {
  if (!name) {
    return 'HB'
  }

  return name
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('')
}

export function AppShell({ children }: { children: ReactNode }) {
  const location = useLocation()
  const navigate = useNavigate()
  const user = authStore((state) => state.user)
  const clearSession = authStore((state) => state.logout)

  async function handleLogout() {
    try {
      await logout()
    } finally {
      clearSession()
      navigate('/login')
    }
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-block">
          <div className="brand-mark">
            <GridIcon />
          </div>
          <div className="brand-copy">
            <h1>BV Nguyễn Tri Phương</h1>
            <p>Oracle live dashboard</p>
          </div>
        </div>

        <div className="sidebar-groups">
          {navSections.map((section) => (
            <section className="sidebar-group" key={section.title}>
              <span className="sidebar-group__label">{section.title}</span>
              <div className="sidebar-links">
                {section.items.map((item) => (
                  <Link
                    className={location.pathname === item.href ? 'nav-link active' : 'nav-link'}
                    key={item.href}
                    to={item.href}
                  >
                    <span className="nav-link__icon">{item.icon}</span>
                    <span className="nav-link__copy">
                      <strong>{item.label}</strong>
                      <small>{item.meta}</small>
                    </span>
                  </Link>
                ))}
              </div>
            </section>
          ))}
        </div>

        <div className="sidebar-footer">
          <div className="user-chip">
            <div className="user-avatar">{getInitials(user?.fullName)}</div>
            <div className="user-copy">
              <strong>{user?.fullName}</strong>
              <p>
                {user ? roleLabels[user.role] : ''} · {user?.departmentName}
              </p>
            </div>
          </div>

          <button className="ghost-button" onClick={handleLogout} type="button">
            Đăng xuất
          </button>
        </div>
      </aside>

      <main className="page-content">
        <div className="page-surface">{children}</div>
      </main>
    </div>
  )
}
