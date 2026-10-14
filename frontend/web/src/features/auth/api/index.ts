import request from '@/utils/request'

export interface LoginPayload {
  username: string
  password: string
}

export interface AuthUser {
  id: number
  username: string
  name?: string
  roleLevel?: number
  [key: string]: unknown
}

export interface LoginResult {
  success: boolean
  token: string
  user: AuthUser
  message?: string
}

export const authApi = {
  login(data: LoginPayload) {
    return request.post<LoginResult>('/auth/login', data) as unknown as Promise<LoginResult>
  },
  logout() {
    return request.post('/auth/logout')
  },
  validate() {
    return request.get<{ valid: boolean }>('/auth/validate') as unknown as Promise<{ valid: boolean }>
  }
}
