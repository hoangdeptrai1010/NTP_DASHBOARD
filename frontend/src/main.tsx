import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { AppRouter } from './router'
import { AuthBootstrap } from './ui/AuthBootstrap'
import './styles/chart-tokens.css'
import './styles.css'

const queryClient = new QueryClient()

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthBootstrap>
        <BrowserRouter>
          <AppRouter />
        </BrowserRouter>
      </AuthBootstrap>
    </QueryClientProvider>
  </React.StrictMode>,
)
