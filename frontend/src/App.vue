<script setup>
import { onMounted, ref } from 'vue'

// systemLoading 控制“后端连接状态”这块区域的加载状态。
const systemLoading = ref(false)

// systemError 保存系统接口请求失败时的错误提示。
const systemError = ref('')

// backendInfo 保存 /api/system/hello 返回的系统信息。
const backendInfo = ref(null)

// userLoading 控制“用户列表”这块区域的加载状态。
const userLoading = ref(false)

// userError 保存用户列表接口请求失败时的错误提示。
const userError = ref('')

// users 保存后端 /api/users 返回的用户数组。
const users = ref([])

/**
 * 调用后端系统接口。
 *
 * 这个接口仍然用于验证前后端是否已经连通。
 */
async function loadBackendInfo() {
  systemLoading.value = true
  systemError.value = ''

  try {
    const response = await fetch('/api/system/hello')
    const result = await response.json()

    // 后端统一约定 code 为 200 表示业务成功。
    if (result.code === 200) {
      backendInfo.value = result.data
    } else {
      systemError.value = result.message || '系统接口返回异常'
    }
  } catch (error) {
    // 后端没启动、代理失败、网络异常都会进入这里。
    systemError.value = '无法连接系统接口，请确认 Java 后端已经启动'
  } finally {
    systemLoading.value = false
  }
}

/**
 * 调用后端用户列表接口。
 *
 * 前端只关心请求 /api/users，Vite 会通过 proxy 转发到 Java 后端。
 * 后端返回的 data 是用户数组，赋值给 users 后，页面表格会自动更新。
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
 * 页面右上角的刷新按钮。
 *
 * Promise.all 可以同时发起两个请求，比一个请求结束后再请求另一个更快。
 */
function refreshPageData() {
  Promise.all([loadBackendInfo(), loadUsers()])
}

// 页面加载完成后，自动请求系统信息和用户列表。
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
          <p class="eyebrow">Step 2</p>
          <h1>用户列表接口</h1>
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

      <section class="panel table-panel">
        <div class="panel-header">
          <div>
            <p class="eyebrow">Controller -> Service -> VO</p>
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
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </section>
  </main>
</template>
