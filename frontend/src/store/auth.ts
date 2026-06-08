import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface UserInfo {
  userId: number
  username: string
  realName: string
  avatar?: string
  roles: string[]
  permissions: string[]
}

interface AuthState {
  token: string
  userInfo: UserInfo | null
  setToken: (token: string) => void
  setUserInfo: (userInfo: UserInfo) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: '',
      userInfo: null,
      setToken: (token) => set({ token }),
      setUserInfo: (userInfo) => set({ userInfo }),
      logout: () => set({ token: '', userInfo: null }),
    }),
    {
      name: 'swim-auth-storage',
    }
  )
)
