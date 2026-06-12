<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

// systemLoading controls the loading state for the backend health panel.
const systemLoading = ref(false)

// systemError stores the error message when /api/system/hello fails.
const systemError = ref('')

// backendInfo stores the data returned by /api/system/hello.
const backendInfo = ref(null)

// userLoading controls the loading state for the user table.
const userLoading = ref(false)

// userError stores the error message for user list/create/update requests.
const userError = ref('')

// users stores the user array returned by the backend.
const users = ref([])

// saveLoading controls the submit button state when creating or updating.
const saveLoading = ref(false)

// saveMessage gives quick feedback after a user is saved.
const saveMessage = ref('')

// editingUserId is null in create mode, and has a user ID in edit mode.
const editingUserId = ref(null)

// userForm is the form data sent to POST /api/users or PUT /api/users/{id}.
const userForm = reactive({
  username: '',
  nickname: '',
  role: '运营管理员',
  status: 'enabled'
})

// A computed value keeps template text simple and avoids duplicated conditions.
const isEditing = computed(() => editingUserId.value !== null)

/**
 * Load backend health information.
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
 * Load the user list from the backend.
 */
async function loadUsers() {
  userLoading.value = true
  userError.value = ''

  try {
    const response = await fetch('/api/users')
    const result = await response.json()

    if (result.code === 200) {
      users.value = result.data
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
 * Submit the form.
 *
 * Create mode uses POST /api/users.
 * Edit mode uses PUT /api/users/{id}.
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
 * Fill the form with table row data and switch to edit mode.
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
 * Reset the form to create mode.
 */
function resetForm() {
  editingUserId.value = null
  userForm.username = ''
  userForm.nickname = ''
  userForm.role = '运营管理员'
  userForm.status = 'enabled'
}

/**
 * Refresh all page data at the same time.
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
          <p class="eyebrow">Step 5</p>
          <h1>编辑用户接口</h1>
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
            <input v-model="userForm.username" placeholder="例如 zhangsan" />
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
            <p class="eyebrow">GET / POST / PUT</p>
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

        <div v-else class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>账号</th>
                <th>昵称</th>
                <th>角色</th>
                <th>状态</th>
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
                <td>
                  <button class="link-button" type="button" @click="startEdit(user)">
                    编辑
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </section>
  </main>
</template>
