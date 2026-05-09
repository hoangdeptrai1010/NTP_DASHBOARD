import type { CSSProperties } from 'react'
import type { ChartType } from '../../hooks/useChart'
import type { RevenuePoint } from '../../types'

interface TrendChartProps {
  data: RevenuePoint[]
  type: ChartType
  color?: string
  ariaLabel: string
}

interface ChartPoint extends RevenuePoint {
  x: number
  y: number
}

const VIEWBOX_WIDTH = 100
const VIEWBOX_HEIGHT = 64
const PADDING = {
  top: 8,
  right: 8,
  bottom: 6,
  left: 8,
}
const GRID_LINES = 4

const currencyFormatter = new Intl.NumberFormat('vi-VN')

function formatCurrency(value: number) {
  return `${currencyFormatter.format(value)} VND`
}



function buildLinePath(points: ChartPoint[]) {
  return points.map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`).join(' ')
}

export function TrendChart({
  data,
  type,
  color = '#1D9E75',
  ariaLabel,
}: TrendChartProps) {
  const innerWidth = VIEWBOX_WIDTH - PADDING.left - PADDING.right
  const innerHeight = VIEWBOX_HEIGHT - PADDING.top - PADDING.bottom
  const maxValue = Math.max(...data.map((point) => point.amount), 1)
  const step = data.length > 1 ? innerWidth / (data.length - 1) : innerWidth
  const barSlotWidth = innerWidth / Math.max(data.length, 1)

  const points = data.map((point, index) => ({
    ...point,
    x: data.length === 1 ? PADDING.left + innerWidth / 2 : PADDING.left + step * index,
    y: PADDING.top + innerHeight - (point.amount / maxValue) * innerHeight,
  }))

  const linePath = buildLinePath(points)

  return (
    <div className={`trend-chart trend-chart--${type}`}>
      <div className="trend-chart__stage">
        <svg
          aria-label={ariaLabel}
          className="trend-chart__svg"
          preserveAspectRatio="none"
          role="img"
          viewBox={`0 0 ${VIEWBOX_WIDTH} ${VIEWBOX_HEIGHT}`}
        >
          <desc>{data.map((point) => `${point.label}: ${formatCurrency(point.amount)}`).join(', ')}</desc>

          {Array.from({ length: GRID_LINES }, (_, index) => {
            const y = PADDING.top + (innerHeight / (GRID_LINES - 1)) * index
            return (
              <line
                key={`grid-${y}`}
                className="trend-chart__grid"
                x1={PADDING.left}
                x2={VIEWBOX_WIDTH - PADDING.right}
                y1={y}
                y2={y}
              />
            )
          })}

          {type === 'bar' ? (
            <g className="trend-chart__bars" key="bar">
              {points.map((point, index) => {
                const barWidth = Math.min(barSlotWidth * 0.52, 11)
                const x = PADDING.left + barSlotWidth * index + (barSlotWidth - barWidth) / 2
                const y = point.y
                const height = PADDING.top + innerHeight - y

                return (
                  <g key={point.label}>
                    <title>{`${point.label}: ${formatCurrency(point.amount)}`}</title>
                    <rect
                      className="trend-chart__bar"
                      fill={color}
                      height={Math.max(height, 1.8)}
                      opacity="0.9"
                      rx="2"
                      style={{ '--index': index } as CSSProperties}
                      width={barWidth}
                      x={x}
                      y={y}
                    />
                  </g>
                )
              })}
            </g>
          ) : null}

          {type === 'line' ? (
            <g className="trend-chart__series" key="line">
              <path
                className="trend-chart__line"
                d={linePath}
                fill="none"
                pathLength={100}
                stroke={color}
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="1.35"
                vectorEffect="non-scaling-stroke"
              />
              {points.map((point, index) => (
                <g key={point.label}>
                  <title>{`${point.label}: ${formatCurrency(point.amount)}`}</title>
                  <circle
                    className="trend-chart__point"
                    cx={point.x}
                    cy={point.y}
                    fill="#ffffff"
                    r="1.7"
                    stroke={color}
                    strokeWidth="1.15"
                    style={{ '--index': index } as CSSProperties}
                    vectorEffect="non-scaling-stroke"
                  />
                </g>
              ))}
            </g>
          ) : null}
        </svg>

        <div
          className="trend-chart__labels"
          key={`labels-${type}`}
          style={{ gridTemplateColumns: `repeat(${Math.max(points.length, 1)}, minmax(0, 1fr))` }}
        >
          {points.map((point, index) => (
            <span
              className="trend-chart__label"
              key={`${point.label}-label`}
              style={{ '--index': index } as CSSProperties}
            >
              {point.label}
            </span>
          ))}
        </div>
      </div>
    </div>
  )
}
