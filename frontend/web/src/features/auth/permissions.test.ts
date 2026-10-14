import { describe, expect, it } from 'vitest'
import { can, type PermissionAction } from './permissions'

describe('role permissions', () => {
  it.each<[number, PermissionAction, boolean]>([
    [1, 'view', true],
    [1, 'refresh', true],
    [1, 'create', false],
    [1, 'edit', false],
    [1, 'delete', false],
    [2, 'create', true],
    [2, 'edit', true],
    [2, 'delete', false],
    [3, 'delete', true],
    [99, 'delete', false]
  ])('role %s %s permission is %s', (roleLevel, action, expected) => {
    expect(can(roleLevel, action)).toBe(expected)
  })
})
