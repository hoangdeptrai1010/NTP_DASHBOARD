import type { ReactNode } from 'react'
import type { KpiCard as KpiCardType } from '../types'

function PatientIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="18" viewBox="0 0 18 18" width="18">
      <circle cx="9" cy="5.2" fill="currentColor" opacity="0.88" r="2.6" />
      <path
        d="M4.8 14.2C5.2 11.8 6.8 10.6 9 10.6C11.2 10.6 12.8 11.8 13.2 14.2"
        stroke="currentColor"
        strokeLinecap="round"
        strokeWidth="1.6"
      />
    </svg>
  )
}

function VisitIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="18" viewBox="0 0 18 18" width="18">
      <rect height="10.5" opacity="0.18" rx="2.2" stroke="currentColor" width="11.5" x="3.3" y="3.7" />
      <path d="M6 9H12" stroke="currentColor" strokeLinecap="round" strokeWidth="1.7" />
      <path d="M9 6V12" stroke="currentColor" strokeLinecap="round" strokeWidth="1.7" />
    </svg>
  )
}

function RevenueIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="18" viewBox="0 0 18 18" width="18">
      <path
        d="M9 2.8V15.2M12.6 5.4C12.6 4.2 11.2 3.2 9.4 3.2C7.4 3.2 6 4.3 6 5.8C6 9 12.6 8.2 12.6 11.6C12.6 13.2 11 14.4 8.8 14.4C7 14.4 5.6 13.6 5.1 12.3"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.6"
      />
    </svg>
  )
}

function BedIcon() {
  return (
    <svg aria-hidden="true" fill="none" height="18" viewBox="0 0 18 18" width="18">
      <path
        d="M3.5 10.5H14.5V13.8M3.5 13.8V6.8H6.8C8.1 6.8 8.9 7.6 8.9 8.9V10.5M8.9 8.4H12.2C13.4 8.4 14.5 9.3 14.5 10.5"
        stroke="currentColor"
        strokeLinecap="round"
        strokeLinejoin="round"
        strokeWidth="1.6"
      />
    </svg>
  )
}

const KPI_META: Record<
  string,
  { accent: 'emerald' | 'blue' | 'orange' | 'violet'; icon: ReactNode; status: string }
> = {
  patients: { accent: 'emerald', icon: <PatientIcon />, status: 'Hoạt động' },
  visits: { accent: 'blue', icon: <VisitIcon />, status: 'Theo dõi' },
  revenue: { accent: 'orange', icon: <RevenueIcon />, status: 'Tài chính' },
  beds: { accent: 'violet', icon: <BedIcon />, status: 'Công suất' },
}

export function KpiCard({ item }: { item: KpiCardType }) {
  const meta = KPI_META[item.key] ?? KPI_META.patients

  return (
    <article className={`kpi-card kpi-card--${meta.accent}`}>
      <div className="kpi-card__head">
        <div className={`kpi-card__icon kpi-card__icon--${meta.accent}`}>{meta.icon}</div>
        <div className="kpi-card__meta">
          <span className="kpi-card__label">{item.label}</span>
          <span className={`kpi-card__tag kpi-card__tag--${meta.accent}`}>{meta.status}</span>
        </div>
      </div>

      <strong>{item.value}</strong>
      <p className={`kpi-support kpi-support--${item.tone}`}>{item.trend}</p>
    </article>
  )
}
