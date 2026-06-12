<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

// systemLoading 控制“后端连接状态”区域的加载状态。
const systemLoading = ref(false)

// systemError 保存 /api/system/hello 请求失败时的错误提示。
const systemError = ref('')

// backendInfo 保存后端系统接口返回的数据。
const backendInfo = ref(null)

// userLoading 控制用户表格的加载状态。
const userLoading = ref(false)

// userError 保存用户列表、新增、编辑、删除接口的错误提示。
const userError = ref('')

// users 保存当前页的用户数组，它来自后端返回的 data.records。
const users = ref([])

// detailLoading 控制用户详情区域的加载状态。
const detailLoading = ref(false)

// detailError 保存 GET /api/users/{id} 查询详情失败时的错误提示。
const detailError = ref('')

// selectedUser 保存当前正在查看详情的用户。
const selectedUser = ref(null)

// saveLoading 控制新增或编辑用户时的提交按钮状态。
const saveLoading = ref(false)

// saveMessage 保存新增、编辑、删除成功后的提示。
const saveMessage = ref('')

// deleteLoadingId 保存正在删除的用户 ID，用来禁用当前行的删除按钮。
const deleteLoadingId = ref(null)

// statusLoadingId 保存正在启用或禁用的用户 ID，用来禁用当前行的状态按钮。
const statusLoadingId = ref(null)

// editingUserId 为 null 表示新增模式，有值表示正在编辑某个用户。
const editingUserId = ref(null)

// searchForm 保存列表筛选条件，会拼到 GET /api/users 的查询参数里。
const searchForm = reactive({
  keyword: '',
  status: ''
})

// pagination 保存分页状态：当前页、每页数量、符合条件的总条数。
const pagination = reactive({
  page: 1,
  size: 5,
  total: 0
})

// userForm 是表单数据，会提交给 POST /api/users 或 PUT /api/users/{id}。
const userForm = reactive({
  username: '',
  nickname: '',
  role: '运营管理员',
  status: 'enabled'
})

// computed 会根据 editingUserId 自动计算当前是否处于编辑模式。
const isEditing = computed(() => editingUserId.value !== null)

// totalPages 根据总条数和每页数量计算总页数，最少显示 1 页。
const totalPages = computed(() => Math.max(1, Math.ceil(pagination.total / pagination.size)))

/**
 * 请求后端健康检查接口，用来确认前后端是否已经连通。
 */
async function loadBackendInfo() {
  systemLoading.value = true
  systemError.value = ''

  try {
    const response = await fetch('/api/system/hello')
    const result = await response.json()

    if (result.code === 200) {
      backendInfo.value = result.data
    } else {
      systemError.value = result.message || '系统接口返回异常'
    }
  } catch (error) {
    systemError.value = '无法连接系统接口，请确认 Java 后端已经启动'
  } finally {
    systemLoading.value = false
  }
}

/**
 * 根据筛选条件和分页参数请求用户列表。
 *
 * URLSearchParams 用来安全拼接查询参数，避免手写字符串时漏掉 ?、& 或编码。
 */
async function loadUsers() {
  userLoading.value = true
  userError.value = ''

  const params = new URLSearchParams()

  if (searchForm.keyword.trim()) {
    params.set('keyword', searchForm.keyword.trim())
  }

  if (searchForm.status) {
    params.set('status', searchForm.status)
  }

  params.set('page', String(pagination.page))
  params.set('size', String(pagination.size))

  try {
    const response = await fetch(`/api/users?${params.toString()}`)
    const result = await response.json()

    if (result.code === 200) {
      users.value = result.data.records
      pagination.total = result.data.total
      pagination.page = result.data.page
      pagination.size = result.data.size
    } else {
      userError.value = result.message || '用户列表接口返回异常'
    }
  } catch (error) {
    userError.value = '无法连接用户列表接口，请确认 Java 后端已经启动'
  } finally {
    userLoading.value = false
  }
}

/**
 * 查询单个用户详情。
 *
 * 这里故意请求 GET /api/users/{id}，不是直接用表格里的 user，
 * 是为了学习真实后台里“列表数据”和“详情数据”分开查询的写法。
 */
async function loadUserDetail(id) {
  detailLoading.value = true
  detailError.value = ''
  selectedUser.value = null

  try {
    const response = await fetch(`/api/users/${id}`)
    const result = await response.json()

    if (result.code === 200) {
      selectedUser.value = result.data
    } else {
      detailError.value = result.message || '用户详情接口返回异常'
    }
  } catch (error) {
    detailError.value = '无法连接用户详情接口，请确认 Java 后端已经启动'
  } finally {
    detailLoading.value = false
  }
}

/**
 * 关闭详情面板。
 */
function clearUserDetail() {
  selectedUser.value = null
  detailError.value = ''
}

/**
 * 提交筛选表单。
 *
 * 新筛选条件一般要从第 1 页开始看，所以这里先把 page 重置成 1。
 */
function submitSearch() {
  pagination.page = 1
  loadUsers()
}

/**
 * 切换页码。
 *
 * 上一页、下一页都会走这个方法，先限制页码范围，再重新请求列表。
 */
function goToPage(page) {
  if (page < 1 || page > totalPages.value || userLoading.value) {
    return
  }

  pagination.page = page
  loadUsers()
}

/**
 * 修改每页数量。
 *
 * 每页数量变了以后，原来的页码可能不存在，所以回到第 1 页重新查询。
 */
function changePageSize() {
  pagination.page = 1
  loadUsers()
}

/**
 * Format backend LocalDateTime string for display.
 *
 * Spring Boot returns LocalDateTime like 2026-06-12T16:00:00, and the page
 * displays it as a local date time string that is easier to read.
 */
function formatDateTime(value) {
  if (!value) {
    return '-'
  }

  return new Date(value).toLocaleString()
}

/**
 * 提交用户表单。
 *
 * 新增模式：POST /api/users
 * 编辑模式：PUT /api/users/{id}
 */
async function saveUser() {
  saveLoading.value = true
  saveMessage.value = ''
  userError.value = ''

  const url = isEditing.value ? `/api/users/${editingUserId.value}` : '/api/users'
  const method = isEditing.value ? 'PUT' : 'POST'

  try {
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(userForm)
    })
    const result = await response.json()

    if (result.code === 200) {
      saveMessage.value = isEditing.value
        ? `已更新用户：${result.data.nickname}`
        : `已新增用户：${result.data.nickname}`

      if (selectedUser.value && selectedUser.value.id === result.data.id) {
        selectedUser.value = result.data
      }

      resetForm()
      await loadUsers()
    } else {
      userError.value = result.message || '保存用户失败'
    }
  } catch (error) {
    userError.value = '保存用户请求失败，请确认 Java 后端已经启动'
  } finally {
    saveLoading.value = false
  }
}

/**
 * 删除用户。
 *
 * 删除当前页最后一条数据后，当前页可能变空，所以会自动回到上一页再查一次。
 */
async function deleteUser(user) {
  const confirmed = window.confirm(`确定删除用户“${user.nickname}”吗？`)

  if (!confirmed) {
    return
  }

  deleteLoadingId.value = user.id
  saveMessage.value = ''
  userError.value = ''

  try {
    const response = await fetch(`/api/users/${user.id}`, {
      method: 'DELETE'
    })
    const result = await response.json()

    if (result.code === 200) {
      if (editingUserId.value === user.id) {
        resetForm()
      }

      if (selectedUser.value && selectedUser.value.id === user.id) {
        clearUserDetail()
      }

      saveMessage.value = `已删除用户：${user.nickname}`
      await loadUsers()

      if (users.value.length === 0 && pagination.page > 1) {
        pagination.page -= 1
        await loadUsers()
      }
    } else {
      userError.value = result.message || '删除用户失败'
    }
  } catch (error) {
    userError.value = '删除用户请求失败，请确认 Java 后端已经启动'
  } finally {
    deleteLoadingId.value = null
  }
}

/**
 * 启用或禁用用户。
 *
 * 这里使用 PATCH /api/users/{id}/status，因为只修改 status 一个字段。
 */
async function changeUserStatus(user) {
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
    const response = await fetch(`/api/users/${user.id}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        status: nextStatus
      })
    })
    const result = await response.json()

    if (result.code === 200) {
      saveMessage.value = `已${actionText}用户：${result.data.nickname}`

      if (selectedUser.value && selectedUser.value.id === result.data.id) {
        selectedUser.value = result.data
      }

      await loadUsers()
    } else {
      userError.value = result.message || `${actionText}用户失败`
    }
  } catch (error) {
    userError.value = `${actionText}用户请求失败，请确认 Java 后端已经启动`
  } finally {
    statusLoadingId.value = null
  }
}

/**
 * 点击表格“编辑”按钮时，把当前行数据填回表单。
 */
function startEdit(user) {
  editingUserId.value = user.id
  saveMessage.value = ''
  userError.value = ''
  userForm.username = user.username
  userForm.nickname = user.nickname
  userForm.role = user.role
  userForm.status = user.status
}

/**
 * 清空筛选条件，并回到第 1 页重新请求用户列表。
 */
function resetSearch() {
  searchForm.keyword = ''
  searchForm.status = ''
  pagination.page = 1
  loadUsers()
}

/**
 * 重置表单，让页面回到新增用户模式。
 */
function resetForm() {
  editingUserId.value = null
  userForm.username = ''
  userForm.nickname = ''
  userForm.role = '运营管理员'
  userForm.status = 'enabled'
}

/**
 * 同时刷新系统状态和用户列表。
 */
function refreshPageData() {
  Promise.all([loadBackendInfo(), loadUsers()])
}

onMounted(() => {
  refreshPageData()
})
</script>

<template>
  <main class="admin-shell">
    <aside class="sidebar">
      <div class="brand">Admin Study</div>
      <nav class="menu">
        <span class="menu-item">系统首页</span>
        <span class="menu-item active">用户管理</span>
        <span class="menu-item">角色管理</span>
      </nav>
    </aside>

    <section class="content">
      <header class="topbar">
        <div>
          <p class="eyebrow">Step 10</p>
          <h1>用户详情查询</h1>
        </div>
        <button class="refresh-button" type="button" @click="refreshPageData">
          重新请求
        </button>
      </header>

      <section class="panel status-panel">
        <h2>后端连接状态</h2>

        <p v-if="systemLoading" class="status muted">正在请求系统接口...</p>

        <p v-else-if="systemError" class="status error">
          {{ systemError }}
        </p>

        <div v-else-if="backendInfo" class="result">
          <p class="status success">{{ backendInfo.message }}</p>
          <dl>
            <dt>接口模块</dt>
            <dd>{{ backendInfo.module }}</dd>
          </dl>
        </div>
      </section>

      <section class="panel detail-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">GET /api/users/{id}</p>
            <h2>用户详情</h2>
          </div>
          <button
            v-if="selectedUser || detailError"
            class="secondary-button"
            type="button"
            @click="clearUserDetail"
          >
            关闭详情
          </button>
        </div>

        <p v-if="detailLoading" class="status muted">正在请求用户详情...</p>

        <p v-else-if="detailError" class="status error">
          {{ detailError }}
        </p>

        <div v-else-if="selectedUser" class="detail-grid">
          <dl>
            <dt>ID</dt>
            <dd>{{ selectedUser.id }}</dd>
            <dt>账号</dt>
            <dd>{{ selectedUser.username }}</dd>
            <dt>昵称</dt>
            <dd>{{ selectedUser.nickname }}</dd>
            <dt>角色</dt>
            <dd>{{ selectedUser.role }}</dd>
            <dt>状态</dt>
            <dd>
              <span class="badge" :class="selectedUser.status">
                {{ selectedUser.status === 'enabled' ? '启用' : '禁用' }}
              </span>
            </dd>
            <dt>创建时间</dt>
            <dd>{{ formatDateTime(selectedUser.createdAt) }}</dd>
            <dt>更新时间</dt>
            <dd>{{ formatDateTime(selectedUser.updatedAt) }}</dd>
          </dl>
        </div>

        <p v-else class="status muted">点击表格里的“查看”，这里会显示单个用户详情。</p>
      </section>

      <section class="panel search-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">GET /api/users?keyword=&status=&page=&size=</p>
            <h2>筛选用户</h2>
          </div>
        </div>

        <form class="search-form" @submit.prevent="submitSearch">
          <label>
            <span>关键字</span>
            <input v-model="searchForm.keyword" placeholder="账号或昵称" />
          </label>

          <label>
            <span>状态</span>
            <select v-model="searchForm.status">
              <option value="">全部状态</option>
              <option value="enabled">启用</option>
              <option value="disabled">禁用</option>
            </select>
          </label>

          <label>
            <span>每页数量</span>
            <select v-model.number="pagination.size" @change="changePageSize">
              <option :value="5">5 条</option>
              <option :value="10">10 条</option>
              <option :value="20">20 条</option>
            </select>
          </label>

          <button class="submit-button" type="submit" :disabled="userLoading">
            {{ userLoading ? '查询中...' : '查询' }}
          </button>

          <button class="secondary-button" type="button" @click="resetSearch">
            重置
          </button>
        </form>
      </section>

      <section class="panel form-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">
              {{ isEditing ? `PUT /api/users/${editingUserId}` : 'POST /api/users' }}
            </p>
            <h2>{{ isEditing ? '编辑用户' : '新增用户' }}</h2>
          </div>
          <button
            v-if="isEditing"
            class="secondary-button"
            type="button"
            @click="resetForm"
          >
            取消编辑
          </button>
        </div>

        <form class="user-form" @submit.prevent="saveUser">
          <label>
            <span>账号</span>
            <input v-model="userForm.username" placeholder="唯一账号，例如 zhangsan" />
          </label>

          <label>
            <span>昵称</span>
            <input v-model="userForm.nickname" placeholder="例如 张三" />
          </label>

          <label>
            <span>角色</span>
            <select v-model="userForm.role">
              <option>超级管理员</option>
              <option>运营管理员</option>
              <option>只读用户</option>
            </select>
          </label>

          <label>
            <span>状态</span>
            <select v-model="userForm.status">
              <option value="enabled">启用</option>
              <option value="disabled">禁用</option>
            </select>
          </label>

          <button class="submit-button" type="submit" :disabled="saveLoading">
            {{ saveLoading ? '提交中...' : isEditing ? '保存修改' : '新增用户' }}
          </button>
        </form>

        <p v-if="saveMessage" class="status success form-message">
          {{ saveMessage }}
        </p>
      </section>

      <section class="panel table-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">GET / POST / PUT / PATCH / DELETE</p>
            <h2>用户管理</h2>
          </div>
          <button class="secondary-button" type="button" @click="loadUsers">
            刷新用户
          </button>
        </div>

        <p v-if="userLoading" class="status muted">正在请求用户列表...</p>

        <p v-else-if="userError" class="status error">
          {{ userError }}
        </p>

        <div v-else>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>账号</th>
                  <th>昵称</th>
                  <th>角色</th>
                  <th>状态</th>
                  <th>更新时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.nickname }}</td>
                  <td>{{ user.role }}</td>
                  <td>
                    <span class="badge" :class="user.status">
                      {{ user.status === 'enabled' ? '启用' : '禁用' }}
                    </span>
                  </td>
                  <td>{{ formatDateTime(user.updatedAt) }}</td>
                  <td>
                    <div class="action-buttons">
                      <button class="link-button" type="button" @click="loadUserDetail(user.id)">
                        查看
                      </button>
                      <button class="link-button" type="button" @click="startEdit(user)">
                        编辑
                      </button>
                      <button
                        class="link-button"
                        type="button"
                        :disabled="statusLoadingId === user.id"
                        @click="changeUserStatus(user)"
                      >
                        {{
                          statusLoadingId === user.id
                            ? '处理中...'
                            : user.status === 'enabled'
                              ? '禁用'
                              : '启用'
                        }}
                      </button>
                      <button
                        class="danger-link-button"
                        type="button"
                        :disabled="deleteLoadingId === user.id"
                        @click="deleteUser(user)"
                      >
                        {{ deleteLoadingId === user.id ? '删除中...' : '删除' }}
                      </button>
                    </div>
                  </td>
                </tr>

                <tr v-if="users.length === 0">
                  <td class="empty-cell" colspan="6">没有匹配的用户</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pagination-bar">
            <span>
              共 {{ pagination.total }} 条，第 {{ pagination.page }} / {{ totalPages }} 页
            </span>
            <div class="pagination-actions">
              <button
                class="secondary-button"
                type="button"
                :disabled="pagination.page <= 1 || userLoading"
                @click="goToPage(pagination.page - 1)"
              >
                上一页
              </button>
              <button
                class="secondary-button"
                type="button"
                :disabled="pagination.page >= totalPages || userLoading"
                @click="goToPage(pagination.page + 1)"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
      </section>
    </section>
  </main>
</template>
