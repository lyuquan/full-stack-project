/**
 * ApiError represents a backend business error.
 *
 * The request reached the backend successfully, but the backend returned
 * code !== 200, for example validation failed or the user does not exist.
 */
export class ApiError extends Error {
  constructor(message) {
    super(message)
    this.name = 'ApiError'
  }
}

/**
 * 保存登录用户的 localStorage key。
 *
 * App.vue 负责写入，api.js 负责读取，这样后续请求可以自动带上 token。
 */
const LOGIN_STORAGE_KEY = 'admin-study-login-user'

/**
 * 从 localStorage 读取登录 token。
 *
 * 如果本地数据格式不对，就返回空字符串，让页面继续按未登录处理。
 */
function getStoredToken() {
  const savedUser = localStorage.getItem(LOGIN_STORAGE_KEY)

  if (!savedUser) {
    return ''
  }

  try {
    const user = JSON.parse(savedUser)
    return user.token || ''
  } catch (error) {
    return ''
  }
}

/**
 * 清理浏览器里保存的登录用户。
 *
 * 后端退出登录成功后，前端也要删除 localStorage，避免继续携带旧 token。
 */
export function clearStoredLoginUser() {
  localStorage.removeItem(LOGIN_STORAGE_KEY)
}

/**
 * Send a request and unwrap the common ApiResponse structure.
 *
 * Backend responses all look like { code, message, data }, so this helper
 * keeps App.vue from repeating response.json() and result.code checks.
 */
async function apiRequest(url, options = {}) {
  const token = getStoredToken()
  const headers = {
    ...(options.headers || {})
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(url, {
    ...options,
    headers
  })
  const result = await response.json()

  if (result.code !== 200) {
    throw new ApiError(result.message || '接口返回异常')
  }

  return result.data
}

/**
 * Send a GET request.
 *
 * GET is usually used for list/detail/options/statistics queries.
 */
export function apiGet(url) {
  return apiRequest(url)
}

/**
 * Send a JSON request with a request body.
 *
 * POST/PUT/PATCH all need the same Content-Type and JSON.stringify logic.
 */
function apiJson(url, method, body) {
  return apiRequest(url, {
    method,
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  })
}

/**
 * Send a POST request.
 *
 * POST is usually used for creating new data.
 */
export function apiPost(url, body) {
  return apiJson(url, 'POST', body)
}

/**
 * Send a PUT request.
 *
 * PUT is usually used for updating a whole record.
 */
export function apiPut(url, body) {
  return apiJson(url, 'PUT', body)
}

/**
 * Send a PATCH request.
 *
 * PATCH is usually used for updating only part of a record.
 */
export function apiPatch(url, body) {
  return apiJson(url, 'PATCH', body)
}

/**
 * Send a DELETE request.
 *
 * DELETE normally does not need a request body in this project.
 */
export function apiDelete(url) {
  return apiRequest(url, {
    method: 'DELETE'
  })
}
