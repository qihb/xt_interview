import request from '@/utils/request'

// 认证相关 API
export const authApi = {
  login(data: { username: string; password: string }) {
    return request.post('/auth/login', data)
  },
  logout() {
    return request.post('/auth/logout')
  },
  validate() {
    return request.get('/auth/validate')
  }
}

// 账号管理 API
export interface Account {
  id: number
  username: string
  name: string
  gender?: string
  phone?: string
  roleLevel: number
  status: number
  isSuper: number
  departmentId: number | null
  departmentName?: string
  createTime?: string
  updateTime?: string
}

export interface AccountPayload {
  username: string
  name: string
  gender: string
  phone?: string
  password?: string
  roleLevel: number
  status: number
  isSuper: number
  departmentId: number | null
}

export const accountApi = {
  getAll() {
    return request.get<Account[]>('/accounts') as unknown as Promise<Account[]>
  },
  getById(id: number) {
    return request.get<Account>(`/accounts/${id}`) as unknown as Promise<Account>
  },
  create(data: AccountPayload) {
    return request.post<Account>('/accounts', data) as unknown as Promise<Account>
  },
  update(id: number, data: AccountPayload) {
    return request.put<Account>(`/accounts/${id}`, data) as unknown as Promise<Account>
  },
  delete(id: number) {
    return request.delete(`/accounts/${id}`)
  }
}

export interface Department {
  id: number
  name: string
  code: string
  description?: string
  status: number
  createTime?: string
  updateTime?: string
}

export interface DepartmentPayload {
  name: string
  code: string
  description?: string
  status: number
}

// 部门管理 API
export const departmentApi = {
  getAll() {
    return request.get<Department[]>('/departments') as unknown as Promise<Department[]>
  },
  getById(id: number) {
    return request.get<Department>(`/departments/${id}`) as unknown as Promise<Department>
  },
  create(data: DepartmentPayload) {
    return request.post<Department>('/departments', data) as unknown as Promise<Department>
  },
  update(id: number, data: DepartmentPayload) {
    return request.put<Department>(`/departments/${id}`, data) as unknown as Promise<Department>
  },
  delete(id: number) {
    return request.delete(`/departments/${id}`)
  }
}
