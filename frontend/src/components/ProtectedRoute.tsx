import { Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/auth'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { token } = useAuthStore()

  if (!token) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

export default ProtectedRoute
