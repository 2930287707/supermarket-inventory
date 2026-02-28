export function getCurrentUser() {
  const raw = localStorage.getItem('user')
  if (!raw) return {}
  try {
    return JSON.parse(raw) || {}
  } catch (e) {
    return {}
  }
}

export function getCurrentRole() {
  const user = getCurrentUser()
  return normalizeRole(user.role)
}

export function hasAnyRole(roles = []) {
  const role = getCurrentRole()
  if (!role) return false
  return roles.map(item => normalizeRole(item)).includes(role)
}

function normalizeRole(role) {
  const normalized = String(role || '').trim().toUpperCase()
  if (normalized === 'MANAGER') {
    return 'ADMIN'
  }
  return normalized
}
