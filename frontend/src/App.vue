<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import {
  apiDelete,
  apiGet,
  apiPatch,
  apiPost,
  apiPut,
  clearStoredLoginUser,
  getStoredLoginUser,
  saveStoredLoginUser
} from './api'

// route reads the current browser path, such as /, /users or /roles.
const route = useRoute()

// router lets JavaScript jump to another frontend route.
const router = useRouter()

// currentUser stores the login user returned by POST /api/auth/login or GET /api/auth/me.
const currentUser = ref(null)

// loginForm stores the username and password typed in the login form.
const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

// loginLoading and loginError control the login panel state.
const loginLoading = ref(false)
const loginError = ref('')

// menus stores sidebar menus returned by GET /api/auth/menus.
const menus = ref([{ key: 'home', label: '系统首页', path: '/', active: true }])

// backendInfo group: data shown on the home page.
const systemLoading = ref(false)
const systemError = ref('')
const backendInfo = ref(null)

// user list state: table data, loading state and error message.
const userLoading = ref(false)
const userError = ref('')
const users = ref([])

// user statistics state: cards on the user management page.
const statsLoading = ref(false)
const statsError = ref('')
const userStats = ref(null)

// roleOptions and statusOptions are select options used by search and user forms.
const roleOptions = ref([])
const roleOptionsError = ref('')
const statusOptions = ref([])
const statusOptionsError = ref('')

// user detail state: data shown after clicking the "view" button in the table.
const detailLoading = ref(false)
const detailError = ref('')
const selectedUser = ref(null)

// user form state: used by both create and edit actions.
const saveLoading = ref(false)
const saveMessage = ref('')
const editingUserId = ref(null)
const userForm = reactive({
  username: '',
  nickname: '',
  role: '运营管理员',
  status: 'enabled'
})

// row action loading state: disables only the row currently being changed.
const deleteLoadingId = ref(null)
const statusLoadingId = ref(null)

// searchForm and pagination are sent to GET /api/users as query parameters.
const searchForm = reactive({
  keyword: '',
  role: '',
  status: ''
})
const pagination = reactive({
  page: 1,
  size: 5,
  total: 0
})

// role page state: data returned by GET /api/roles.
const roles = ref([])
const roleLoading = ref(false)
const roleError = ref('')

// role detail state: data returned by GET /api/roles/{code}.
const roleDetailLoading = ref(false)
const roleDetailError = ref('')
const selectedRole = ref(null)

// role form state: used by POST /api/roles to create a new role.
const roleForm = reactive({
  code: '',
  name: '',
  description: '',
  permissionCount: 0
})
const roleSaveLoading = ref(false)
const roleSaveMessage = ref('')

// user:write is the backend permission code for changing user data.
const USER_WRITE_PERMISSION = 'user:write'

// isEditing decides whether the user form should create or update a user.
const isEditing = computed(() => editingUserId.value !== null)

// totalPages keeps pagination display stable even when total is 0.
const totalPages = computed(() => Math.max(1, Math.ceil(pagination.total / pagination.size)))

// currentMenu is matched from the current browser path and visible menu list.
const currentMenu = computed(() => {
  return menus.value.find((menu) => getMenuPath(menu) === route.path) || menus.value[0]
})

// currentMenuKey is used by the sidebar to highlight the active menu.
const currentMenuKey = computed(() => {
  return currentMenu.value ? currentMenu.value.key : 'home'
})

// canManageUsers controls user create/edit/delete buttons on the frontend.
const canManageUsers = computed(() => hasPermission(USER_WRITE_PERMISSION))

/**
 * Check whether the current login user owns a permission code.
 *
 * The frontend uses this for button state. The backend still does the real
 * security check through interceptors.
 */
function hasPermission(permissionCode) {
  return Boolean(
    currentUser.value &&
      Array.isArray(currentUser.value.permissions) &&
      currentUser.value.permissions.includes(permissionCode)
  )
}

/**
 * Convert route/menu data into a safe path.
 *
 * The fallback keeps old hot-reload menu data from breaking router.push.
 */
function getMenuPath(menu) {
  if (menu && menu.path) {
    return menu.path
  }

  if (menu && menu.key === 'users') {
    return '/users'
  }

  if (menu && menu.key === 'roles') {
    return '/roles'
  }

  return '/'
}

/**
 * Keep the browser path inside the current user's visible menu range.
 */
function normalizeCurrentMenu() {
  if (menus.value.length === 0) {
    router.replace('/')
    return
  }

  const exists = menus.value.some((menu) => getMenuPath(menu) === route.path)

  if (!exists) {
    router.replace(getMenuPath(menus.value[0]))
  }
}

/**
 * Select a sidebar menu and jump to its route path.
 */
function selectMenu(menu) {
  router.push(getMenuPath(menu))
}

/**
 * Read the backend health endpoint for the home page.
 */
async function loadBackendInfo() {
  systemLoading.value = true
  systemError.value = ''

  try {
    backendInfo.value = await apiGet('/api/system/hello')
  } catch (error) {
    systemError.value = getApiErrorMessage(error, '无法连接系统接口，请确认 Java 后端已经启动')
  } finally {
    systemLoading.value = false
  }
}

/**
 * Request sidebar menus visible to the current login user.
 */
async function loadMenus() {
  if (!currentUser.value) {
    menus.value = [{ key: 'home', label: '系统首页', path: '/', active: true }]
    normalizeCurrentMenu()
    return
  }

  menus.value = await apiGet('/api/auth/menus')
  normalizeCurrentMenu()
}

/**
 * Load users with search conditions and pagination.
 */
async function loadUsers() {
  userLoading.value = true
  userError.value = ''

  const params = new URLSearchParams()

  if (searchForm.keyword.trim()) {
    params.set('keyword', searchForm.keyword.trim())
  }

  if (searchForm.role) {
    params.set('role', searchForm.role)
  }

  if (searchForm.status) {
    params.set('status', searchForm.status)
  }

  params.set('page', String(pagination.page))
  params.set('size', String(pagination.size))

  try {
    const data = await apiGet(`/api/users?${params.toString()}`)

    users.value = data.records
    pagination.total = data.total
    pagination.page = data.page
    pagination.size = data.size
  } catch (error) {
    userError.value = getApiErrorMessage(error, '无法连接用户列表接口，请确认 Java 后端已经启动')
  } finally {
    userLoading.value = false
  }
}

/**
 * Load user statistics cards.
 */
async function loadUserStats() {
  statsLoading.value = true
  statsError.value = ''

  try {
    userStats.value = await apiGet('/api/users/stats')
  } catch (error) {
    statsError.value = getApiErrorMessage(error, '无法连接用户统计接口，请确认 Java 后端已经启动')
  } finally {
    statsLoading.value = false
  }
}

/**
 * Load role select options for search and user forms.
 */
async function loadRoleOptions() {
  roleOptionsError.value = ''

  try {
    roleOptions.value = await apiGet('/api/users/roles')
  } catch (error) {
    roleOptionsError.value = getApiErrorMessage(error, '无法连接角色选项接口，请确认 Java 后端已经启动')
  }
}

/**
 * Load status select options for search and user forms.
 */
async function loadStatusOptions() {
  statusOptionsError.value = ''

  try {
    statusOptions.value = await apiGet('/api/users/statuses')
  } catch (error) {
    statusOptionsError.value = getApiErrorMessage(error, '无法连接状态选项接口，请确认 Java 后端已经启动')
  }
}

/**
 * Load role list data for the role management page.
 */
async function loadRoles() {
  if (!currentUser.value) {
    roles.value = []
    return
  }

  roleLoading.value = true
  roleError.value = ''

  try {
    roles.value = await apiGet('/api/roles')
  } catch (error) {
    roleError.value = getApiErrorMessage(error, '无法连接角色列表接口，请确认 Java 后端已经启动')
  } finally {
    roleLoading.value = false
  }
}

/**
 * Load a single role detail by code.
 */
async function loadRoleDetail(code) {
  roleDetailLoading.value = true
  roleDetailError.value = ''
  selectedRole.value = null

  try {
    selectedRole.value = await apiGet(`/api/roles/${code}`)
  } catch (error) {
    roleDetailError.value = getApiErrorMessage(error, '无法连接角色详情接口，请确认 Java 后端已经启动')
  } finally {
    roleDetailLoading.value = false
  }
}

/**
 * Load a single user detail by id.
 */
async function loadUserDetail(id) {
  detailLoading.value = true
  detailError.value = ''
  selectedUser.value = null

  try {
    selectedUser.value = await apiGet(`/api/users/${id}`)
  } catch (error) {
    detailError.value = getApiErrorMessage(error, '无法连接用户详情接口，请确认 Java 后端已经启动')
  } finally {
    detailLoading.value = false
  }
}

/**
 * Clear the user detail panel.
 */
function clearUserDetail() {
  selectedUser.value = null
  detailError.value = ''
}

/**
 * Clear the role detail panel.
 */
function clearRoleDetail() {
  selectedRole.value = null
  roleDetailError.value = ''
}

/**
 * Create a role by POST /api/roles.
 */
async function saveRole() {
  roleSaveLoading.value = true
  roleSaveMessage.value = ''
  roleError.value = ''

  try {
    const savedRole = await apiPost('/api/roles', {
      ...roleForm,
      permissionCount: Number(roleForm.permissionCount)
    })

    roleSaveMessage.value = `已新增角色：${savedRole.name}`
    selectedRole.value = savedRole
    resetRoleForm()
    await loadRoles()
  } catch (error) {
    roleError.value = getApiErrorMessage(error, '保存角色请求失败，请确认 Java 后端已经启动')
  } finally {
    roleSaveLoading.value = false
  }
}

/**
 * Reset the role form to empty create mode.
 */
function resetRoleForm() {
  roleForm.code = ''
  roleForm.name = ''
  roleForm.description = ''
  roleForm.permissionCount = 0
}

/**
 * Search from the first page when conditions change.
 */
function submitSearch() {
  pagination.page = 1
  loadUsers()
}

/**
 * Jump to another page in the user table.
 */
function goToPage(page) {
  if (page < 1 || page > totalPages.value || userLoading.value) {
    return
  }

  pagination.page = page
  loadUsers()
}

/**
 * Change page size and reload from the first page.
 */
function changePageSize() {
  pagination.page = 1
  loadUsers()
}

/**
 * Convert request errors into page messages.
 */
function getApiErrorMessage(error, networkMessage) {
  if (error && error.name === 'ApiError') {
    return error.message
  }

  return networkMessage
}

/**
 * Login and then load protected page data.
 */
async function login() {
  loginLoading.value = true
  loginError.value = ''

  try {
    currentUser.value = await apiPost('/api/auth/login', loginForm)
    saveStoredLoginUser(currentUser.value)
    await loadProtectedData()
  } catch (error) {
    loginError.value = getApiErrorMessage(error, '登录请求失败，请确认 Java 后端已经启动')
  } finally {
    loginLoading.value = false
  }
}

/**
 * Logout from backend and clear frontend protected data.
 */
async function logout() {
  try {
    await apiPost('/api/auth/logout')
  } finally {
    currentUser.value = null
    loginError.value = ''
    clearStoredLoginUser()
    clearProtectedData()
  }
}

/**
 * Restore login state from localStorage and verify it with GET /api/auth/me.
 */
async function restoreLoginUser() {
  const savedUser = getStoredLoginUser()

  if (!savedUser) {
    return
  }

  try {
    const loginUser = await apiGet('/api/auth/me')

    currentUser.value = {
      ...savedUser,
      ...loginUser,
      token: savedUser.token
    }
    saveStoredLoginUser(currentUser.value)
  } catch (error) {
    currentUser.value = null
    clearStoredLoginUser()
  }
}

/**
 * Clear data that should only be visible after login.
 */
function clearProtectedData() {
  users.value = []
  userStats.value = null
  roleOptions.value = []
  statusOptions.value = []
  roles.value = []
  menus.value = [{ key: 'home', label: '系统首页', path: '/', active: true }]
  selectedUser.value = null
  selectedRole.value = null
  resetRoleForm()
  userError.value = ''
  statsError.value = ''
  roleOptionsError.value = ''
  statusOptionsError.value = ''
  detailError.value = ''
  roleError.value = ''
  roleDetailError.value = ''
  roleSaveMessage.value = ''
  router.replace('/')
}

/**
 * Save a user in create or edit mode.
 */
async function saveUser() {
  if (!canManageUsers.value) {
    userError.value = '当前账号没有操作权限'
    return
  }

  saveLoading.value = true
  saveMessage.value = ''
  userError.value = ''

  const url = isEditing.value ? `/api/users/${editingUserId.value}` : '/api/users'

  try {
    const savedUser = isEditing.value ? await apiPut(url, userForm) : await apiPost(url, userForm)

    saveMessage.value = isEditing.value
      ? `已更新用户：${savedUser.nickname}`
      : `已新增用户：${savedUser.nickname}`

    if (selectedUser.value && selectedUser.value.id === savedUser.id) {
      selectedUser.value = savedUser
    }

    resetForm()
    await refreshUserData()
  } catch (error) {
    userError.value = getApiErrorMessage(error, '保存用户请求失败，请确认 Java 后端已经启动')
  } finally {
    saveLoading.value = false
  }
}

/**
 * Delete a user and refresh the current page.
 */
async function deleteUser(user) {
  if (!canManageUsers.value) {
    userError.value = '当前账号没有操作权限'
    return
  }

  const confirmed = window.confirm(`确定删除用户“${user.nickname}”吗？`)

  if (!confirmed) {
    return
  }

  deleteLoadingId.value = user.id
  saveMessage.value = ''
  userError.value = ''

  try {
    await apiDelete(`/api/users/${user.id}`)

    if (editingUserId.value === user.id) {
      resetForm()
    }

    if (selectedUser.value && selectedUser.value.id === user.id) {
      clearUserDetail()
    }

    saveMessage.value = `已删除用户：${user.nickname}`
    await refreshUserData()

    if (users.value.length === 0 && pagination.page > 1) {
      pagination.page -= 1
      await refreshUserData()
    }
  } catch (error) {
    userError.value = getApiErrorMessage(error, '删除用户请求失败，请确认 Java 后端已经启动')
  } finally {
    deleteLoadingId.value = null
  }
}

/**
 * Enable or disable a user by PATCH /api/users/{id}/status.
 */
async function changeUserStatus(user) {
  if (!canManageUsers.value) {
    userError.value = '当前账号没有操作权限'
    return
  }

  const nextStatus = user.status === 'enabled' ? 'disabled' : 'enabled'
  const actionText = nextStatus === 'enabled' ? '启用' : '禁用'
  const confirmed = window.confirm(`确定${actionText}用户“${user.nickname}”吗？`)

  if (!confirmed) {
    return
  }

  statusLoadingId.value = user.id
  saveMessage.value = ''
  userError.value = ''

  try {
    const updatedUser = await apiPatch(`/api/users/${user.id}/status`, {
      status: nextStatus
    })

    saveMessage.value = `已${actionText}用户：${updatedUser.nickname}`

    if (selectedUser.value && selectedUser.value.id === updatedUser.id) {
      selectedUser.value = updatedUser
    }

    await refreshUserData()
  } catch (error) {
    userError.value = getApiErrorMessage(error, `${actionText}用户请求失败，请确认 Java 后端已经启动`)
  } finally {
    statusLoadingId.value = null
  }
}

/**
 * Put the selected row into the edit form.
 */
function startEdit(user) {
  if (!canManageUsers.value) {
    userError.value = '当前账号没有操作权限'
    return
  }

  editingUserId.value = user.id
  saveMessage.value = ''
  userError.value = ''
  userForm.username = user.username
  userForm.nickname = user.nickname
  userForm.role = user.role
  userForm.status = user.status
}

/**
 * Clear search fields and reload the first page.
 */
function resetSearch() {
  searchForm.keyword = ''
  searchForm.role = ''
  searchForm.status = ''
  pagination.page = 1
  loadUsers()
}

/**
 * Reset the user form to create mode.
 */
function resetForm() {
  editingUserId.value = null
  userForm.username = ''
  userForm.nickname = ''
  userForm.role = '运营管理员'
  userForm.status = 'enabled'
}

/**
 * Refresh the user table and statistics together.
 */
function refreshUserData() {
  if (!currentUser.value) {
    clearProtectedData()
    return Promise.resolve()
  }

  return Promise.all([loadUserStats(), loadUsers()])
}

/**
 * Load data that requires a login token.
 */
function loadProtectedData() {
  return Promise.all([loadMenus(), loadRoleOptions(), loadStatusOptions(), loadRoles(), refreshUserData()])
}

/**
 * Refresh the current page and shared data.
 */
function refreshPageData() {
  if (!currentUser.value) {
    clearProtectedData()
    return Promise.all([loadBackendInfo()])
  }

  return Promise.all([loadBackendInfo(), loadProtectedData()])
}

onMounted(async () => {
  await restoreLoginUser()
  await refreshPageData()
})

// When the address changes manually, keep it inside visible menu permissions.
watch(
  () => route.path,
  () => {
    normalizeCurrentMenu()
  }
)
</script>

<template>
  <main class="admin-shell">
    <aside class="sidebar">
      <div class="brand">Admin Study</div>
      <nav class="menu">
        <button
          v-for="menu in menus"
          :key="menu.key"
          class="menu-item"
          :class="{ active: menu.key === currentMenuKey }"
          type="button"
          @click="selectMenu(menu)"
        >
          {{ menu.label }}
        </button>
      </nav>
    </aside>

    <section class="content">
      <header class="topbar">
        <div>
          <p class="eyebrow">{{ currentMenu ? getMenuPath(currentMenu) : '/' }}</p>
          <h1>{{ currentMenu ? currentMenu.label : '系统首页' }}</h1>
        </div>
        <button class="refresh-button" type="button" @click="refreshPageData">
          重新请求
        </button>
      </header>

      <section class="panel login-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">POST /api/auth/login</p>
            <h2>登录后台</h2>
          </div>
          <button
            v-if="currentUser"
            class="secondary-button"
            type="button"
            @click="logout"
          >
            退出登录
          </button>
        </div>

        <form v-if="!currentUser" class="login-form" @submit.prevent="login">
          <label>
            <span>账号</span>
            <input v-model="loginForm.username" autocomplete="username" />
          </label>

          <label>
            <span>密码</span>
            <input v-model="loginForm.password" type="password" autocomplete="current-password" />
          </label>

          <button class="submit-button" type="submit" :disabled="loginLoading">
            {{ loginLoading ? '登录中...' : '登录' }}
          </button>
        </form>

        <p v-if="loginError" class="status error form-message">
          {{ loginError }}
        </p>

        <p v-if="currentUser" class="status success">
          当前登录：{{ currentUser.nickname }} / {{ currentUser.role }}
        </p>
      </section>

      <RouterView
        :system-loading="systemLoading"
        :system-error="systemError"
        :backend-info="backendInfo"
        :stats-loading="statsLoading"
        :stats-error="statsError"
        :user-stats="userStats"
        :detail-loading="detailLoading"
        :detail-error="detailError"
        :selected-user="selectedUser"
        :search-form="searchForm"
        :role-options="roleOptions"
        :status-options="statusOptions"
        :pagination="pagination"
        :user-form="userForm"
        :is-editing="isEditing"
        :editing-user-id="editingUserId"
        :save-loading="saveLoading"
        :can-manage-users="canManageUsers"
        :save-message="saveMessage"
        :role-options-error="roleOptionsError"
        :status-options-error="statusOptionsError"
        :user-loading="userLoading"
        :user-error="userError"
        :users="users"
        :delete-loading-id="deleteLoadingId"
        :status-loading-id="statusLoadingId"
        :total-pages="totalPages"
        :role-loading="roleLoading"
        :role-error="roleError"
        :roles="roles"
        :role-detail-loading="roleDetailLoading"
        :role-detail-error="roleDetailError"
        :selected-role="selectedRole"
        :role-form="roleForm"
        :role-save-loading="roleSaveLoading"
        :role-save-message="roleSaveMessage"
        @load-user-stats="loadUserStats"
        @clear-user-detail="clearUserDetail"
        @submit-search="submitSearch"
        @change-page-size="changePageSize"
        @reset-search="resetSearch"
        @reset-form="resetForm"
        @save-user="saveUser"
        @load-users="loadUsers"
        @load-user-detail="loadUserDetail"
        @start-edit="startEdit"
        @change-user-status="changeUserStatus"
        @delete-user="deleteUser"
        @go-to-page="goToPage"
        @load-roles="loadRoles"
        @load-role-detail="loadRoleDetail"
        @clear-role-detail="clearRoleDetail"
        @save-role="saveRole"
        @reset-role-form="resetRoleForm"
      />
    </section>
  </main>
</template>
