export type PermissionAction = 'view' | 'refresh' | 'create' | 'edit' | 'delete'

const permissions: Record<number, PermissionAction[]> = {
  1: ['view', 'refresh'],
  2: ['view', 'refresh', 'create', 'edit'],
  3: ['view', 'refresh', 'create', 'edit', 'delete']
}

export function can(roleLevel: number | null | undefined, action: PermissionAction): boolean {
  return roleLevel != null && permissions[roleLevel]?.includes(action) === true
}
