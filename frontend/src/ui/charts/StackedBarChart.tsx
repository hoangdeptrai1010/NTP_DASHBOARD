import { type CSSProperties } from 'react'
import type { DeptCompareItem } from '../../types'

interface StackedBarChartProps {
  data: DeptCompareItem[]
  title: string
}

export function StackedBarChart({ data, title }: StackedBarChartProps) {
  const maxTotal = Math.max(...data.map((d) => d.bhytAmount + d.patientAmount), 1)

  return (
    <div className="panel stacked-bar-panel">
      <h3 className="panel-title" style={{ marginBottom: '1.5rem', fontSize: '1rem' }}>{title}</h3>
      <div className="stacked-bar-legend">
        <div className="stacked-bar-legend-item">
          <span className="stacked-bar-legend-dot" style={{ backgroundColor: '#378ADD' }} />
          BHYT Chi trả
        </div>
        <div className="stacked-bar-legend-item">
          <span className="stacked-bar-legend-dot" style={{ backgroundColor: '#FF8A65' }} />
          Bệnh nhân tự trả
        </div>
      </div>
      
      <div className="stacked-bar-list">
        {data.length === 0 ? (
           <div style={{ color: 'var(--muted)', fontSize: '0.875rem' }}>Không có dữ liệu cho thời gian này.</div>
        ) : data.map((item, index) => {
          const total = item.bhytAmount + item.patientAmount
          const bhytPct = total > 0 ? (item.bhytAmount / maxTotal) * 100 : 0
          const patientPct = total > 0 ? (item.patientAmount / maxTotal) * 100 : 0

          return (
            <div 
              key={item.deptName} 
              className="stacked-bar-row"
              style={{ '--bar-delay': `${index * 80}ms` } as CSSProperties}
            >
              <div className="stacked-bar-header">
                <span className="stacked-bar-dept">{item.deptName}</span>
                <span className="stacked-bar-total">{total.toLocaleString('vi-VN')} VND</span>
              </div>
              <div className="stacked-bar-track">
                <div 
                  className="stacked-bar-fill stacked-bar-fill--bhyt" 
                  style={{ '--bar-target-width': `${bhytPct}%` } as CSSProperties} 
                  title={`BHYT: ${item.bhytAmount.toLocaleString('vi-VN')}`} 
                />
                <div 
                  className="stacked-bar-fill stacked-bar-fill--patient" 
                  style={{ '--bar-target-width': `${patientPct}%` } as CSSProperties} 
                  title={`Tự trả: ${item.patientAmount.toLocaleString('vi-VN')}`} 
                />
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
