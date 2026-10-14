import request from '@/utils/request'

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
  unassignUsers?: boolean
}

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
