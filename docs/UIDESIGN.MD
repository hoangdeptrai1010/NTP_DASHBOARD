# Hướng dẫn thiết kế giao diện chọn biểu đồ

> **Dự án:** Hospital Dashboard — Bệnh viện Nguyễn Tri Phương  
> **Stack:** React 19 · Chart.js 4 · Vanilla CSS Variables  
> **Phạm vi:** Biểu đồ cột (Bar) · Biểu đồ đường (Line) · Biểu đồ vùng (Area) · Biểu đồ tròn/Donut (Pie/Doughnut)

---

## Mục lục

1. [Nguyên tắc thiết kế](#1-nguyên-tắc-thiết-kế)
2. [Design tokens — màu sắc & kích thước](#2-design-tokens--màu-sắc--kích-thước)
3. [Bộ chọn loại biểu đồ (Chart Type Switcher)](#3-bộ-chọn-loại-biểu-đồ-chart-type-switcher)
4. [Biểu đồ cột — Bar Chart](#4-biểu-đồ-cột--bar-chart)
5. [Biểu đồ đường — Line Chart](#5-biểu-đồ-đường--line-chart)
6. [Biểu đồ vùng — Area Chart](#6-biểu-đồ-vùng--area-chart)
7. [Biểu đồ tròn / Donut — Pie & Doughnut](#7-biểu-đồ-tròn--donut--pie--doughnut)
8. [ChartCard — component bọc ngoài](#8-chartcard--component-bọc-ngoài)
9. [Hook useChart — quản lý state biểu đồ](#9-hook-usechart--quản-lý-state-biểu-đồ)
10. [Lắp vào Dashboard Page](#10-lắp-vào-dashboard-page)
11. [Responsive — tablet & desktop](#11-responsive--tablet--desktop)
12. [Quy tắc UX cần tuân theo](#12-quy-tắc-ux-cần-tuân-theo)
13. [Checklist trước khi ship](#13-checklist-trước-khi-ship)

---

## 1. Nguyên tắc thiết kế

### Triết lý Lovable cho biểu đồ

```
✅  Borders, không shadows       → dùng border: 1px solid #eceae4
✅  Nền trắng thuần (#ffffff)    → card chart nền trắng trên cream #f7f4ed
✅  Màu có nghĩa, không màu đẹp  → xanh lá = tốt, vàng = cảnh báo, đỏ = khẩn
✅  Typography rõ ràng           → label 11px #5f5f5d, value 13px #1c1c1c bold
✅  Transition mượt              → 300ms ease khi chuyển loại biểu đồ
❌  Gradient nền loè loẹt        → không dùng
❌  Shadow phức tạp              → không dùng box-shadow nặng
❌  Quá nhiều màu trong 1 chart  → tối đa 6 màu trong palette
```

### Màu ngữ nghĩa của bệnh viện

| Ý nghĩa | Màu | Hex | Dùng khi |
|---------|-----|-----|----------|
| Doanh thu / Tốt | Xanh lá | `#1D9E75` | Biểu đồ doanh thu, chỉ số đạt mục tiêu |
| Bệnh nhân | Xanh dương | `#378ADD` | Số lượt khám, thống kê bệnh nhân |
| Cảnh báo | Vàng cam | `#EF9F27` | Công suất giường > 80%, tồn kho thấp |
| Khẩn cấp | Đỏ cam | `#D85A30` | ICU, cấp cứu, tồn kho khẩn |
| Trung tính | Charcoal | `#1c1c1c` | Biểu đồ tổng hợp, dữ liệu chính |
| Phụ | Xám nhạt | `#b4b2a9` | Grid lines, axis labels |

---

## 2. Design tokens — màu sắc & kích thước

Đặt vào `src/styles/chart-tokens.css` và import vào `main.tsx`:

```css
/* src/styles/chart-tokens.css */
:root {
  /* ── Màu chính dashboard ── */
  --color-cream:     #f7f4ed;
  --color-charcoal:  #1c1c1c;
  --color-offwhite:  #fcfbf8;
  --color-muted:     #5f5f5d;
  --color-border:    #eceae4;

  /* ── Palette biểu đồ ── */
  --chart-green:     #1D9E75;
  --chart-blue:      #378ADD;
  --chart-amber:     #EF9F27;
  --chart-red:       #D85A30;
  --chart-purple:    #7B68C8;
  --chart-teal:      #2ABFBF;

  /* ── Chart card ── */
  --chart-card-bg:        #ffffff;
  --chart-card-border:    #eceae4;
  --chart-card-radius:    10px;
  --chart-card-padding:   16px;

  /* ── Grid & axis ── */
  --chart-grid-color:     rgba(28, 28, 28, 0.05);
  --chart-axis-color:     #888780;
  --chart-axis-font-size: 11px;

  /* ── Switcher pill ── */
  --switcher-bg:          #f7f4ed;
  --switcher-border:      #eceae4;
  --switcher-active-bg:   #1c1c1c;
  --switcher-active-text: #fcfbf8;
  --switcher-radius:      6px;
  --switcher-height:      30px;
}
```

---

## 3. Bộ chọn loại biểu đồ (Chart Type Switcher)

### Giao diện

```
┌─────────────────────────────┐
│  Doanh thu theo thời gian   │    ← cc-title
│  Theo năm — 12 tháng        │    ← cc-sub
└─────────────────────────────┘
                        [Cột] [Đường] [Vùng]   ← switcher pills
```

### Component TypeScript

```tsx
// src/components/charts/ChartTypeSwitcher.tsx
import { useState } from 'react'
import './ChartTypeSwitcher.css'

export type ChartType = 'bar' | 'line' | 'area' | 'pie' | 'doughnut'

interface Props {
  /** Danh sách loại biểu đồ muốn hiện */
  options: ChartType[]
  value:   ChartType
  onChange: (type: ChartType) => void
}

const LABEL: Record<ChartType, string> = {
  bar:      'Cột',
  line:     'Đường',
  area:     'Vùng',
  pie:      'Tròn',
  doughnut: 'Donut',
}

/** Icon nhỏ 14×14 cho từng loại */
const ICON: Record<ChartType, React.ReactNode> = {
  bar: (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <rect x="1" y="6" width="3" height="7" rx="1" fill="currentColor"/>
      <rect x="5.5" y="3" width="3" height="10" rx="1" fill="currentColor"/>
      <rect x="10" y="1" width="3" height="12" rx="1" fill="currentColor"/>
    </svg>
  ),
  line: (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <polyline points="1,11 4,6 7,8 10,3 13,5"
        stroke="currentColor" strokeWidth="1.5"
        strokeLinecap="round" strokeLinejoin="round" fill="none"/>
    </svg>
  ),
  area: (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M1 11 L4 6 L7 8 L10 3 L13 5 L13 13 L1 13 Z"
        fill="currentColor" opacity="0.3"/>
      <polyline points="1,11 4,6 7,8 10,3 13,5"
        stroke="currentColor" strokeWidth="1.5"
        strokeLinecap="round" strokeLinejoin="round" fill="none"/>
    </svg>
  ),
  pie: (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <path d="M7 1 A6 6 0 0 1 13 7 L7 7 Z" fill="currentColor"/>
      <path d="M7 7 L13 7 A6 6 0 1 1 7 1 Z" fill="currentColor" opacity="0.35"/>
    </svg>
  ),
  doughnut: (
    <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
      <circle cx="7" cy="7" r="5.5" stroke="currentColor" strokeWidth="3"
        strokeDasharray="17 17" strokeLinecap="round"/>
    </svg>
  ),
}

export function ChartTypeSwitcher({ options, value, onChange }: Props) {
  return (
    <div className="chart-type-switcher" role="group" aria-label="Chọn loại biểu đồ">
      {options.map(type => (
        <button
          key={type}
          className={`ct-btn ${value === type ? 'active' : ''}`}
          onClick={() => onChange(type)}
          aria-pressed={value === type}
          title={LABEL[type]}
        >
          {ICON[type]}
          <span>{LABEL[type]}</span>
        </button>
      ))}
    </div>
  )
}
```

### CSS của Switcher

```css
/* src/components/charts/ChartTypeSwitcher.css */
.chart-type-switcher {
  display: flex;
  gap: 4px;
  align-items: center;
}

.ct-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: var(--switcher-height);
  padding: 0 10px;
  border: 1px solid var(--switcher-border);
  border-radius: var(--switcher-radius);
  background: transparent;
  color: var(--color-muted);
  font-size: 12px;
  font-weight: 400;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
  white-space: nowrap;
  /* Không dùng shadow — đúng triết lý Lovable */
}

.ct-btn:hover {
  background: var(--color-cream);
  color: var(--color-charcoal);
}

.ct-btn.active {
  background: var(--switcher-active-bg);
  color: var(--switcher-active-text);
  border-color: var(--switcher-active-bg);
}

.ct-btn svg {
  flex-shrink: 0;
  opacity: 0.8;
}

.ct-btn.active svg {
  opacity: 1;
}
```

---

## 4. Biểu đồ cột — Bar Chart

**Dùng khi:** So sánh giá trị giữa các kỳ (tháng, quý, khoa). Tốt nhất cho dữ liệu rời rạc.

**Ví dụ:** Doanh thu 12 tháng, số bệnh nhân theo khoa.

```tsx
// src/components/charts/BarChartRenderer.tsx
import { useEffect, useRef } from 'react'
import Chart from 'chart.js/auto'
import type { ChartDataset } from 'chart.js'

interface Props {
  labels:  string[]
  datasets: ChartDataset<'bar'>[]
  height?: number
}

export function BarChartRenderer({ labels, datasets, height = 200 }: Props) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const chartRef  = useRef<Chart | null>(null)

  useEffect(() => {
    if (!canvasRef.current) return

    // ⚠️ QUAN TRỌNG: destroy chart cũ trước khi tạo mới
    // Nếu quên bước này → Canvas is already in use error
    chartRef.current?.destroy()

    chartRef.current = new Chart(canvasRef.current, {
      type: 'bar',
      data: { labels, datasets },
      options: {
        responsive:          true,
        maintainAspectRatio: false,
        animation: { duration: 400, easing: 'easeInOutQuart' },
        plugins: {
          legend: { display: datasets.length > 1 },
          tooltip: {
            backgroundColor: '#1c1c1c',
            titleColor:      '#fcfbf8',
            bodyColor:       '#b4b2a9',
            padding:         10,
            cornerRadius:    6,
            displayColors:   false,
          },
        },
        scales: {
          x: {
            grid:  { display: false },
            ticks: {
              color:    '#888780',
              font:     { size: 11 },
            },
            border: { display: false },
          },
          y: {
            grid:  { color: 'rgba(28,28,28,0.05)', lineWidth: 1 },
            ticks: {
              color:    '#888780',
              font:     { size: 11 },
              maxTicksLimit: 5,
            },
            border: { display: false, dash: [4, 4] },
          },
        },
      },
    })

    // Cleanup khi component unmount
    return () => { chartRef.current?.destroy() }
  }, [labels, datasets])

  return (
    <div style={{ position: 'relative', height }}>
      <canvas ref={canvasRef} />
    </div>
  )
}
```

### Cấu hình dataset mẫu cho Bar

```tsx
// Doanh thu 12 tháng — màu charcoal theo Lovable
const revenueBarDataset = {
  label:           'Doanh thu (tỷ đồng)',
  data:            apiData.values,             // số thật từ API
  backgroundColor: 'rgba(28, 28, 28, 0.85)',   // charcoal
  borderRadius:    4,                           // bo góc nhẹ
  borderSkipped:   false,
  hoverBackgroundColor: '#1c1c1c',
}

// Nhiều dataset (grouped bar) — ví dụ BHYT vs tự trả
const groupedDatasets = [
  {
    label:           'BHYT',
    data:            apiData.bhyt,
    backgroundColor: '#1D9E75',
    borderRadius:    3,
  },
  {
    label:           'Tự trả',
    data:            apiData.tuTra,
    backgroundColor: '#378ADD',
    borderRadius:    3,
  },
]
```

---

## 5. Biểu đồ đường — Line Chart

**Dùng khi:** Hiển thị xu hướng liên tục theo thời gian. Tốt cho trend phân tích.

**Ví dụ:** Số bệnh nhân theo tuần trong năm, tỷ lệ hài lòng theo tháng.

```tsx
// src/components/charts/LineChartRenderer.tsx
import { useEffect, useRef } from 'react'
import Chart from 'chart.js/auto'

interface Props {
  labels:  string[]
  data:    number[]
  color?:  string
  height?: number
}

export function LineChartRenderer({
  labels,
  data,
  color  = '#1c1c1c',
  height = 200,
}: Props) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const chartRef  = useRef<Chart | null>(null)

  useEffect(() => {
    if (!canvasRef.current) return
    chartRef.current?.destroy()

    chartRef.current = new Chart(canvasRef.current, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label:            'Giá trị',
          data,
          borderColor:      color,
          borderWidth:      2,
          backgroundColor:  'transparent',
          // Điểm tròn nhỏ trên mỗi data point
          pointRadius:      3,
          pointHoverRadius: 5,
          pointBackgroundColor:  color,
          pointBorderColor:      '#ffffff',
          pointBorderWidth:      1.5,
          // Đường cong mượt (0 = thẳng, 0.4 = cong vừa)
          tension: 0.4,
          fill: false,
        }],
      },
      options: {
        responsive:          true,
        maintainAspectRatio: false,
        animation: { duration: 400 },
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor:  '#1c1c1c',
            titleColor:       '#fcfbf8',
            bodyColor:        '#b4b2a9',
            padding:          10,
            cornerRadius:     6,
            intersect:        false,  // tooltip xuất hiện khi hover gần đường
            mode:             'index',
          },
        },
        scales: {
          x: {
            grid:  { display: false },
            ticks: { color: '#888780', font: { size: 11 } },
            border: { display: false },
          },
          y: {
            grid:  { color: 'rgba(28,28,28,0.05)' },
            ticks: { color: '#888780', font: { size: 11 }, maxTicksLimit: 5 },
            border: { display: false },
          },
        },
        // Cross-hair khi hover
        interaction: {
          mode:      'index',
          intersect: false,
        },
      },
    })

    return () => { chartRef.current?.destroy() }
  }, [labels, data, color])

  return (
    <div style={{ position: 'relative', height }}>
      <canvas ref={canvasRef} />
    </div>
  )
}
```

---

## 6. Biểu đồ vùng — Area Chart

**Dùng khi:** Tương tự Line nhưng muốn nhấn mạnh khối lượng/volume. Phù hợp doanh thu tích lũy.

**Ví dụ:** Tổng doanh thu tích lũy theo tháng, số lượt khám tổng cộng.

```tsx
// Area Chart = Line Chart với fill: true + gradient background
// Không cần component riêng — dùng lại LineChartRenderer với prop area=true

export function AreaChartRenderer({
  labels,
  data,
  color  = '#1c1c1c',
  height = 200,
}: Props) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const chartRef  = useRef<Chart | null>(null)

  useEffect(() => {
    if (!canvasRef.current) return
    chartRef.current?.destroy()

    const ctx = canvasRef.current.getContext('2d')!

    // ── Gradient fill từ màu chính → trong suốt ──────────────────────
    const gradient = ctx.createLinearGradient(0, 0, 0, height)
    gradient.addColorStop(0,   hexToRgba(color, 0.15))  // 15% opacity ở đỉnh
    gradient.addColorStop(0.7, hexToRgba(color, 0.03))  // 3% ở giữa
    gradient.addColorStop(1,   hexToRgba(color, 0))     // trong suốt ở đáy

    chartRef.current = new Chart(canvasRef.current, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          data,
          borderColor:     color,
          borderWidth:     2,
          backgroundColor: gradient,   // ← gradient thay vì transparent
          fill:            true,        // ← bật fill
          tension:         0.4,
          pointRadius:     3,
          pointHoverRadius: 5,
          pointBackgroundColor: color,
          pointBorderColor:    '#ffffff',
          pointBorderWidth:    1.5,
        }],
      },
      options: {
        responsive:          true,
        maintainAspectRatio: false,
        animation: { duration: 400 },
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: '#1c1c1c',
            titleColor:      '#fcfbf8',
            bodyColor:       '#b4b2a9',
            padding:         10,
            cornerRadius:    6,
            mode:            'index',
            intersect:       false,
          },
        },
        scales: {
          x: {
            grid:  { display: false },
            ticks: { color: '#888780', font: { size: 11 } },
            border: { display: false },
          },
          y: {
            grid:  { color: 'rgba(28,28,28,0.05)' },
            ticks: { color: '#888780', font: { size: 11 }, maxTicksLimit: 5 },
            border: { display: false },
          },
        },
      },
    })

    return () => { chartRef.current?.destroy() }
  }, [labels, data, color, height])

  return (
    <div style={{ position: 'relative', height }}>
      <canvas ref={canvasRef} />
    </div>
  )
}

// Helper chuyển hex → rgba
function hexToRgba(hex: string, alpha: number): string {
  const r = parseInt(hex.slice(1, 3), 16)
  const g = parseInt(hex.slice(3, 5), 16)
  const b = parseInt(hex.slice(5, 7), 16)
  return `rgba(${r}, ${g}, ${b}, ${alpha})`
}
```

---

## 7. Biểu đồ tròn / Donut — Pie & Doughnut

**Dùng khi:** Hiển thị tỷ lệ phần trăm, phân bổ. Tối đa 6 phần — nhiều hơn khó đọc.

**Ví dụ:** Phân bổ doanh thu theo nhóm dịch vụ, tỷ lệ BHYT vs tự trả vs miễn giảm.

```tsx
// src/components/charts/PieChartRenderer.tsx
import { useEffect, useRef } from 'react'
import Chart from 'chart.js/auto'

// Màu cố định theo thứ tự — dùng nhất quán toàn dashboard
export const PIE_COLORS = [
  '#1c1c1c',   // charcoal — phần lớn nhất
  '#1D9E75',   // xanh lá
  '#378ADD',   // xanh dương
  '#EF9F27',   // vàng cam
  '#D85A30',   // đỏ cam
  '#7B68C8',   // tím
]

interface PieSegment {
  label: string
  value: number   // số thật từ API (tiền, lượt, %)
}

interface Props {
  segments:  PieSegment[]
  type?:     'pie' | 'doughnut'
  showLegend?: boolean
  height?:   number
}

export function PieChartRenderer({
  segments,
  type        = 'doughnut',
  showLegend  = true,
  height      = 180,
}: Props) {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const chartRef  = useRef<Chart | null>(null)

  const total = segments.reduce((s, seg) => s + seg.value, 0)

  useEffect(() => {
    if (!canvasRef.current) return
    chartRef.current?.destroy()

    chartRef.current = new Chart(canvasRef.current, {
      type,
      data: {
        labels:   segments.map(s => s.label),
        datasets: [{
          data:            segments.map(s => s.value),
          backgroundColor: PIE_COLORS.slice(0, segments.length),
          borderWidth:     0,         // không viền giữa các phần
          hoverOffset:     6,         // phần được hover sẽ nổi ra 6px
        }],
      },
      options: {
        responsive:          true,
        maintainAspectRatio: false,
        animation: { duration: 400 },
        cutout: type === 'doughnut' ? '65%' : 0,  // lỗ donut 65%
        plugins: {
          legend: { display: false },  // Legend tự render bên dưới
          tooltip: {
            backgroundColor: '#1c1c1c',
            titleColor:      '#fcfbf8',
            bodyColor:       '#b4b2a9',
            padding:         10,
            cornerRadius:    6,
            callbacks: {
              // Hiện % thay vì số tuyệt đối trong tooltip
              label: (ctx) => {
                const pct = ((ctx.raw as number) / total * 100).toFixed(1)
                return ` ${ctx.label}: ${pct}%`
              },
            },
          },
        },
      },
    })

    return () => { chartRef.current?.destroy() }
  }, [segments, type])

  return (
    <div className="pie-chart-wrapper">
      <div style={{ position: 'relative', height }}>
        <canvas ref={canvasRef} />
      </div>

      {/* Legend tự vẽ — không dùng Chart.js legend mặc định */}
      {showLegend && (
        <div className="pie-legend">
          {segments.map((seg, i) => {
            const pct = (seg.value / total * 100).toFixed(1)
            return (
              <div key={seg.label} className="pie-legend-item">
                <span
                  className="pie-legend-dot"
                  style={{ background: PIE_COLORS[i] }}
                />
                <span className="pie-legend-label">{seg.label}</span>
                <span className="pie-legend-pct">{pct}%</span>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
```

### CSS của Pie legend

```css
/* src/components/charts/PieChart.css */
.pie-chart-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  align-items: center;
}

/* Trên tablet: stack dọc */
@media (max-width: 900px) {
  .pie-chart-wrapper {
    grid-template-columns: 1fr;
  }
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.pie-legend-item {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12px;
  color: var(--color-charcoal);
}

.pie-legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  flex-shrink: 0;
}

.pie-legend-label {
  flex: 1;
  color: var(--color-muted);
  font-size: 11px;
  line-height: 1.3;
}

.pie-legend-pct {
  font-weight: 500;
  font-size: 12px;
  color: var(--color-charcoal);
  font-variant-numeric: tabular-nums;
}
```

---

## 8. ChartCard — component bọc ngoài

Mọi biểu đồ đều được bọc trong `ChartCard`. Đây là component dùng chung, không viết lại card CSS cho từng biểu đồ.

```tsx
// src/components/charts/ChartCard.tsx
import { ReactNode } from 'react'
import { ChartTypeSwitcher, ChartType } from './ChartTypeSwitcher'
import './ChartCard.css'

interface Props {
  title:       string
  subtitle?:   string
  children:    ReactNode

  /** Truyền vào nếu chart này có switcher */
  chartType?:   ChartType
  typeOptions?: ChartType[]
  onTypeChange?: (t: ChartType) => void

  /** Cho phép chiếm full width (2 cột) */
  wide?: boolean
}

export function ChartCard({
  title,
  subtitle,
  children,
  chartType,
  typeOptions,
  onTypeChange,
  wide = false,
}: Props) {
  return (
    <div className={`chart-card ${wide ? 'chart-card--wide' : ''}`}>
      <div className="chart-card__header">
        <div className="chart-card__meta">
          <h3 className="chart-card__title">{title}</h3>
          {subtitle && <p className="chart-card__sub">{subtitle}</p>}
        </div>
        {typeOptions && chartType && onTypeChange && (
          <ChartTypeSwitcher
            options={typeOptions}
            value={chartType}
            onChange={onTypeChange}
          />
        )}
      </div>
      <div className="chart-card__body">
        {children}
      </div>
    </div>
  )
}
```

### CSS của ChartCard

```css
/* src/components/charts/ChartCard.css */
.chart-card {
  background:    var(--chart-card-bg);
  border:        1px solid var(--chart-card-border);
  border-radius: var(--chart-card-radius);
  padding:       var(--chart-card-padding);
  /* Không dùng box-shadow — đúng Lovable style */
}

.chart-card--wide {
  grid-column: 1 / -1;   /* Chiếm full width trong grid */
}

.chart-card__header {
  display:         flex;
  align-items:     flex-start;
  justify-content: space-between;
  gap:             12px;
  margin-bottom:   16px;
}

.chart-card__meta {
  flex: 1;
  min-width: 0;   /* Cho phép text wrap */
}

.chart-card__title {
  font-size:      13px;
  font-weight:    500;
  color:          var(--color-charcoal);
  letter-spacing: -0.2px;
  margin:         0;
}

.chart-card__sub {
  font-size:   11px;
  color:       var(--color-muted);
  margin:      3px 0 0;
}

.chart-card__body {
  /* Không set height ở đây — để renderer tự xác định */
}
```

---

## 9. Hook useChart — quản lý state biểu đồ

Tập trung state loại biểu đồ + kỳ thời gian vào một hook để tái sử dụng:

```tsx
// src/hooks/useChart.ts
import { useState, useCallback } from 'react'
import type { ChartType } from '../components/charts/ChartTypeSwitcher'

export type Period = 'week' | 'quarter' | 'year'

interface UseChartReturn {
  chartType: ChartType
  period:    Period
  setChartType: (t: ChartType) => void
  setPeriod:    (p: Period)    => void
}

/**
 * Hook quản lý state chọn loại biểu đồ và kỳ thời gian.
 *
 * Dùng chung cho mọi ChartCard trong dashboard.
 * Mỗi ChartCard có thể dùng instance hook riêng (không share state).
 */
export function useChart(
  defaultType:   ChartType = 'bar',
  defaultPeriod: Period    = 'year',
): UseChartReturn {
  const [chartType, setChartTypeState] = useState<ChartType>(defaultType)
  const [period,    setPeriodState]    = useState<Period>(defaultPeriod)

  const setChartType = useCallback((t: ChartType) => {
    setChartTypeState(t)
  }, [])

  const setPeriod = useCallback((p: Period) => {
    setPeriodState(p)
  }, [])

  return { chartType, period, setChartType, setPeriod }
}
```

---

## 10. Lắp vào Dashboard Page

### Cách render đúng loại biểu đồ dựa trên `chartType`

```tsx
// src/components/charts/ChartRenderer.tsx
// Component trung gian — nhận chartType và render đúng renderer

import { BarChartRenderer }  from './BarChartRenderer'
import { LineChartRenderer } from './LineChartRenderer'
import { AreaChartRenderer } from './AreaChartRenderer'
import { PieChartRenderer }  from './PieChartRenderer'
import type { ChartType }    from './ChartTypeSwitcher'

interface Props {
  chartType: ChartType
  labels:    string[]
  data:      number[]
  color?:    string
  height?:   number
}

export function ChartRenderer({ chartType, labels, data, color, height }: Props) {
  switch (chartType) {
    case 'bar':      return <BarChartRenderer  labels={labels} datasets={[{ data, backgroundColor: color ?? '#1c1c1c', borderRadius: 4 }]} height={height} />
    case 'line':     return <LineChartRenderer labels={labels} data={data} color={color} height={height} />
    case 'area':     return <AreaChartRenderer labels={labels} data={data} color={color} height={height} />
    case 'pie':      return <PieChartRenderer  segments={labels.map((l, i) => ({ label: l, value: data[i] }))} type="pie"      height={height} />
    case 'doughnut': return <PieChartRenderer  segments={labels.map((l, i) => ({ label: l, value: data[i] }))} type="doughnut" height={height} />
    default:         return null
  }
}
```

### Dùng trong DashboardPage

```tsx
// src/pages/DashboardPage.tsx (phần chart)
import { useChart }       from '../hooks/useChart'
import { ChartCard }      from '../components/charts/ChartCard'
import { ChartRenderer }  from '../components/charts/ChartRenderer'
import { useDashboard }   from '../hooks/useDashboard'

export function DashboardPage() {
  // Mỗi chart có state riêng
  const revenueChart = useChart('bar',      'year')
  const patientChart = useChart('line',     'year')

  // Data thật từ API — không mock
  const { revenueData, patientData, isLoading } = useDashboard({
    period: revenueChart.period,
  })

  if (isLoading) return <DashboardSkeleton />

  return (
    <div className="dashboard-grid">

      {/* Biểu đồ doanh thu — full width, có switcher 3 loại */}
      <ChartCard
        title="Doanh thu theo thời gian"
        subtitle={getPeriodLabel(revenueChart.period)}
        chartType={revenueChart.chartType}
        typeOptions={['bar', 'line', 'area']}
        onTypeChange={revenueChart.setChartType}
        wide
      >
        <ChartRenderer
          chartType={revenueChart.chartType}
          labels={revenueData.labels}       // ["T1", "T2", ...] từ API
          data={revenueData.values}          // [1.2, 1.4, ...] từ API (tỷ đồng)
          color="#1D9E75"
          height={200}
        />
      </ChartCard>

      {/* Biểu đồ phân bổ doanh thu — donut/pie switcher */}
      <ChartCard
        title="Phân bổ doanh thu"
        subtitle="Theo nhóm dịch vụ"
        chartType={/* donut state */}
        typeOptions={['doughnut', 'pie']}
        onTypeChange={/* setter */}
      >
        <PieChartRenderer
          segments={revenueData.breakdown}  // [{label, value}] từ API
          type="doughnut"
          height={180}
        />
      </ChartCard>

      {/* Thống kê bệnh nhân — line/bar switcher */}
      <ChartCard
        title="Lượt khám theo thời gian"
        subtitle={getPeriodLabel(patientChart.period)}
        chartType={patientChart.chartType}
        typeOptions={['line', 'bar', 'area']}
        onTypeChange={patientChart.setChartType}
      >
        <ChartRenderer
          chartType={patientChart.chartType}
          labels={patientData.labels}
          data={patientData.values}
          color="#378ADD"
          height={180}
        />
      </ChartCard>

    </div>
  )
}

function getPeriodLabel(period: string): string {
  return { week: 'Theo tuần — 7 ngày gần nhất', quarter: 'Theo quý — 3 tháng gần nhất', year: 'Theo năm — 12 tháng gần nhất' }[period] ?? ''
}
```

### Grid layout cho dashboard

```css
/* src/pages/DashboardPage.css */
.dashboard-grid {
  display:               grid;
  grid-template-columns: 1fr 1fr;
  gap:                   12px;
  padding:               20px 24px;
}

/* Tablet: vẫn 2 cột nhưng gap nhỏ hơn */
@media (max-width: 1024px) and (min-width: 768px) {
  .dashboard-grid {
    gap: 10px;
    padding: 16px 20px;
  }
}

/* Mobile: 1 cột */
@media (max-width: 767px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
    padding: 12px 16px;
  }
}
```

---

## 11. Responsive — tablet & desktop

| Breakpoint | Grid | Sidebar | Switcher |
|------------|------|---------|----------|
| ≥ 1280px (desktop lớn) | 2 cột | 220px cố định | Labels + icons |
| 1024–1279px (desktop nhỏ) | 2 cột | 220px cố định | Labels + icons |
| 768–1023px (tablet) | 2 cột | Thu lại 60px | Chỉ icons |
| < 768px (mobile) | 1 cột | Ẩn (hamburger) | Chỉ icons |

### Ẩn label trên tablet nhỏ

```css
/* ChartTypeSwitcher.css */
@media (max-width: 900px) {
  .ct-btn span {
    display: none;   /* Chỉ hiện icon, ẩn text */
  }
  .ct-btn {
    padding: 0 8px;
    width:   30px;
  }
}
```

---

## 12. Quy tắc UX cần tuân theo

### Chuyển loại biểu đồ

```
✅  Destroy chart cũ TRƯỚC khi init chart mới
    → chartRef.current?.destroy() trong useEffect cleanup

✅  Animation 400ms khi chuyển type
    → options.animation: { duration: 400, easing: 'easeInOutQuart' }

✅  Giữ nguyên data khi chuyển type
    → chỉ đổi kiểu render, không gọi lại API

❌  Không dùng setTimeout để delay destroy
    → gây Canvas is already in use error
```

### Tooltip

```
✅  Background #1c1c1c, text #fcfbf8         → tương phản rõ
✅  Hiển thị đơn vị: "1.4 tỷ đồng"          → không chỉ "1.4"
✅  mode: 'index', intersect: false          → dễ hover trên line/area
✅  cornerRadius: 6                          → nhất quán với border-radius hệ thống
```

### Empty state

```tsx
// Khi API trả về mảng rỗng
{data.length === 0 ? (
  <div className="chart-empty">
    <svg>...</svg>
    <p>Chưa có dữ liệu cho kỳ này</p>
  </div>
) : (
  <ChartRenderer ... />
)}
```

```css
.chart-empty {
  height:          180px;
  display:         flex;
  flex-direction:  column;
  align-items:     center;
  justify-content: center;
  gap:             8px;
  color:           var(--color-muted);
  font-size:       13px;
}
```

### Loading skeleton

```tsx
// Dùng CSS animation thay vì thư viện nặng
function ChartSkeleton({ height = 200 }: { height?: number }) {
  return (
    <div
      className="chart-skeleton"
      style={{ height }}
      aria-label="Đang tải biểu đồ..."
    />
  )
}
```

```css
.chart-skeleton {
  background: linear-gradient(
    90deg,
    #eceae4 25%,
    #f7f4ed 50%,
    #eceae4 75%
  );
  background-size: 200% 100%;
  border-radius:   6px;
  animation:       shimmer 1.5s infinite;
}

@keyframes shimmer {
  0%   { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
```

---

## 13. Checklist trước khi ship

### Chart.js setup
- [ ] `import Chart from 'chart.js/auto'` — không import từng module riêng lẻ để tránh "missing scale" error
- [ ] Mỗi `useEffect` có `return () => { chartRef.current?.destroy() }` ở cuối
- [ ] Không gắn `new Chart()` trực tiếp vào DOM element mà không qua `useRef`

### Dữ liệu
- [ ] Tất cả `labels` và `data` đến từ API thật — không có hardcode hay mock
- [ ] Handle `isLoading` → hiện skeleton
- [ ] Handle `data.length === 0` → hiện empty state
- [ ] Đơn vị hiển thị đúng trong tooltip (tỷ đồng, lượt, %)

### UI/UX
- [ ] Switcher highlight đúng loại đang active
- [ ] Transition 400ms khi chuyển chart type — không flash đột ngột
- [ ] Tooltip rõ, không bị cắt xén ở mép card
- [ ] Pie chart tối đa 6 phần — nhiều hơn cần gộp "Khác"
- [ ] Chart không bị squish trên tablet (maintainAspectRatio: false + height cố định)

### Accessibility
- [ ] Canvas có `aria-label` mô tả nội dung
- [ ] Switcher buttons có `aria-pressed` đúng state
- [ ] Màu sắc đủ contrast ratio 4.5:1 (kiểm tra bằng Chrome DevTools)

---

*CHART_UI_GUIDE.md — Hospital Dashboard — Bệnh viện Nguyễn Tri Phương — v1.0*