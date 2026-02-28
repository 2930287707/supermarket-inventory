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
  return (user.role || '').toUpperCase()
}

export function hasAnyRole(roles = []) {
  const role = getCurrentRole()
  if (!role) return false
  return roles.map(item => String(item).toUpperCase()).includes(role)
}
