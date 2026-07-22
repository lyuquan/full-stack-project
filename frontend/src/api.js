/**
 * 后端业务错误。
 *
 * 请求已经到达后端，但后端返回 code !== 200 时，会抛出这个错误。
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
 * App.vue 负责写入登录用户，api.js 负责读取 token 并自动放到请求头里。
 */
const LOGIN_STORAGE_KEY = 'admin-study-login-user'

/**
 * 保存登录用户到 localStorage。
 *
 * 登录成功后调用它，刷新页面后才能从浏览器里找回 token。
 */
export function saveStoredLoginUser(user) {
  localStorage.setItem(LOGIN_STORAGE_KEY, JSON.stringify(user))
}

/**
 * 从 localStorage 读取登录用户。
 *
 * 如果本地数据格式不对，就清理掉并返回 null。
 */
export function getStoredLoginUser() {
  const savedUser = localStorage.getItem(LOGIN_STORAGE_KEY)

  if (!savedUser) {
    return null
  }

  try {
    return JSON.parse(savedUser)
  } catch (error) {
    clearStoredLoginUser()
    return null
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
 * 从 localStorage 读取登录 token。
 *
 * apiRequest 会用它自动给请求加 Authorization 请求头。
 */
function getStoredToken() {
  const user = getStoredLoginUser()

  return user ? user.token || '' : ''
}

/**
 * 发送请求并拆开统一响应结构。
 *
 * 后端统一返回 { code, message, data }，所以这里集中处理 JSON 解析和业务错误判断。
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
 * 发送 GET 请求。
 *
 * GET 通常用于列表、详情、选项、统计等查询接口。
 */
export function apiGet(url) {
  return apiRequest(url)
}

/**
 * 发送带 JSON 请求体的请求。
 *
 * POST、PUT、PATCH 都需要设置 Content-Type，并把对象转成 JSON 字符串。
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
 * 发送 POST 请求。
 *
 * POST 通常用于新增数据、登录、退出等动作。
 */
export function apiPost(url, body) {
  return apiJson(url, 'POST', body)
}

/**
 * 发送 PUT 请求。
 *
 * PUT 通常用于完整更新一条记录。
 */
export function apiPut(url, body) {
  return apiJson(url, 'PUT', body)
}

/**
 * 发送 PATCH 请求。
 *
 * PATCH 通常用于只更新一条记录里的部分字段。
 */
export function apiPatch(url, body) {
  return apiJson(url, 'PATCH', body)
}

/**
 * 发送 DELETE 请求。
 *
 * 这个项目里的删除接口暂时不需要请求体。
 */
export function apiDelete(url) {
  return apiRequest(url, {
    method: 'DELETE'
  })
}
