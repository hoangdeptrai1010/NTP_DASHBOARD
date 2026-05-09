import { useState } from 'react'

export type ChartType = 'bar' | 'line'

export function useChart(defaultType: ChartType = 'bar') {
  const [chartType, setChartType] = useState<ChartType>(defaultType)

  return {
    chartType,
    setChartType,
  }
}
