import { afterEach, vi } from 'vitest'
import { config } from '@vue/test-utils'
import { createPinia } from 'pinia'

config.global.plugins = [createPinia()]

afterEach(() => {
  localStorage.clear()
  vi.restoreAllMocks()
})
