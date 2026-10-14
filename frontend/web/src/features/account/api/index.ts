import request from '@/utils/request'

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

export interface AccountPageParams {
  username?: string
  status?: number
  roleLevel?: number
  page: number
  size: number
}

export interface PageResponse<T> {
  list: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
}

export const accountApi = {
  getAll() {
    return request.get<Account[]>('/accounts') as unknown as Promise<Account[]>
  },
  getById(id: number) {
    return request.get<Account>(`/accounts/${id}`) as unknown as Promise<Account>
  },
  page(params: AccountPageParams) {
    return request.get<PageResponse<Account>>('/accounts/page', { params }) as unknown as Promise<PageResponse<Account>>
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
