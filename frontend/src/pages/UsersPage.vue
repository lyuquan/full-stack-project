<script setup>
// UsersPage only renders the user-management screen.
// Data and request methods are still owned by App.vue in this step, so we can
// focus on learning route page extraction before moving business logic.
defineOptions({
  inheritAttrs: false
})

const props = defineProps({
  statsLoading: Boolean,
  statsError: String,
  userStats: Object,
  detailLoading: Boolean,
  detailError: String,
  selectedUser: Object,
  searchForm: Object,
  roleOptions: Array,
  statusOptions: Array,
  pagination: Object,
  userForm: Object,
  isEditing: Boolean,
  editingUserId: [Number, String],
  saveLoading: Boolean,
  canManageUsers: Boolean,
  saveMessage: String,
  roleOptionsError: String,
  statusOptionsError: String,
  userLoading: Boolean,
  userError: String,
  users: Array,
  deleteLoadingId: [Number, String],
  statusLoadingId: [Number, String],
  totalPages: Number
})

const emit = defineEmits([
  'loadUserStats',
  'clearUserDetail',
  'submitSearch',
  'changePageSize',
  'resetSearch',
  'resetForm',
  'saveUser',
  'loadUsers',
  'loadUserDetail',
  'startEdit',
  'changeUserStatus',
  'deleteUser',
  'goToPage'
])

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
 * Convert enabled/disabled to the label shown in the table and detail panel.
 */
function getStatusLabel(status) {
  const matchedStatus = props.statusOptions.find((option) => option.value === status)

  if (matchedStatus) {
    return matchedStatus.label
  }

  return status === 'enabled' ? '启用' : '禁用'
}

</script>

<template>
  <section class="panel stats-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">GET /api/users/stats</p>
        <h2>用户统计</h2>
      </div>
      <button class="secondary-button" type="button" @click="emit('loadUserStats')">
        刷新统计
      </button>
    </div>

    <p v-if="statsLoading" class="status muted">正在请求用户统计...</p>

    <p v-else-if="statsError" class="status error">
      {{ statsError }}
    </p>

    <div v-else-if="userStats" class="stats-grid">
      <div class="stats-item">
        <span>用户总数</span>
        <strong>{{ userStats.totalCount }}</strong>
      </div>
      <div class="stats-item">
        <span>启用用户</span>
        <strong>{{ userStats.enabledCount }}</strong>
      </div>
      <div class="stats-item">
        <span>禁用用户</span>
        <strong>{{ userStats.disabledCount }}</strong>
      </div>
      <div class="stats-item">
        <span>超级管理员</span>
        <strong>{{ userStats.superAdminCount }}</strong>
      </div>
      <div class="stats-item">
        <span>运营管理员</span>
        <strong>{{ userStats.operatorCount }}</strong>
      </div>
      <div class="stats-item">
        <span>只读用户</span>
        <strong>{{ userStats.readonlyCount }}</strong>
      </div>
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
        @click="emit('clearUserDetail')"
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
            {{ getStatusLabel(selectedUser.status) }}
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
        <p class="eyebrow">GET /api/users?keyword=&role=&status=&page=&size=</p>
        <h2>筛选用户</h2>
      </div>
    </div>

    <form class="search-form" @submit.prevent="emit('submitSearch')">
      <label>
        <span>关键字</span>
        <input v-model="searchForm.keyword" placeholder="账号或昵称" />
      </label>

      <label>
        <span>状态</span>
        <select v-model="searchForm.status">
          <option value="">全部状态</option>
          <option
            v-for="status in statusOptions"
            :key="status.value"
            :value="status.value"
          >
            {{ status.label }}
          </option>
        </select>
      </label>

      <label>
        <span>角色</span>
        <select v-model="searchForm.role">
          <option value="">全部角色</option>
          <option
            v-for="role in roleOptions"
            :key="role.value"
            :value="role.value"
          >
            {{ role.label }}
          </option>
        </select>
      </label>

      <label>
        <span>每页数量</span>
        <select v-model.number="pagination.size" @change="emit('changePageSize')">
          <option :value="5">5 条</option>
          <option :value="10">10 条</option>
          <option :value="20">20 条</option>
        </select>
      </label>

      <button class="submit-button" type="submit" :disabled="userLoading">
        {{ userLoading ? '查询中...' : '查询' }}
      </button>

      <button class="secondary-button" type="button" @click="emit('resetSearch')">
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
        @click="emit('resetForm')"
      >
        取消编辑
      </button>
    </div>

    <form class="user-form" @submit.prevent="emit('saveUser')">
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
          <option
            v-for="role in roleOptions"
            :key="role.value"
            :value="role.value"
          >
            {{ role.label }}
          </option>
        </select>
      </label>

      <label>
        <span>状态</span>
        <select v-model="userForm.status">
          <option
            v-for="status in statusOptions"
            :key="status.value"
            :value="status.value"
          >
            {{ status.label }}
          </option>
        </select>
      </label>

      <button class="submit-button" type="submit" :disabled="saveLoading || !canManageUsers">
        {{ saveLoading ? '提交中...' : isEditing ? '保存修改' : '新增用户' }}
      </button>
    </form>

    <p v-if="saveMessage" class="status success form-message">
      {{ saveMessage }}
    </p>

    <p v-if="roleOptionsError" class="status error form-message">
      {{ roleOptionsError }}
    </p>

    <p v-if="statusOptionsError" class="status error form-message">
      {{ statusOptionsError }}
    </p>
  </section>

  <section class="panel table-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">GET / POST / PUT / PATCH / DELETE</p>
        <h2>用户管理</h2>
      </div>
      <button class="secondary-button" type="button" @click="emit('loadUsers')">
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
                  {{ getStatusLabel(user.status) }}
                </span>
              </td>
              <td>{{ formatDateTime(user.updatedAt) }}</td>
              <td>
                <div class="action-buttons">
                  <button class="link-button" type="button" @click="emit('loadUserDetail', user.id)">
                    查看
                  </button>
                  <button
                    class="link-button"
                    type="button"
                    :disabled="!canManageUsers"
                    @click="emit('startEdit', user)"
                  >
                    编辑
                  </button>
                  <button
                    class="link-button"
                    type="button"
                    :disabled="statusLoadingId === user.id || !canManageUsers"
                    @click="emit('changeUserStatus', user)"
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
                    :disabled="deleteLoadingId === user.id || !canManageUsers"
                    @click="emit('deleteUser', user)"
                  >
                    {{ deleteLoadingId === user.id ? '删除中...' : '删除' }}
                  </button>
                </div>
              </td>
            </tr>

            <tr v-if="users.length === 0">
              <td class="empty-cell" colspan="7">没有匹配的用户</td>
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
            @click="emit('goToPage', pagination.page - 1)"
          >
            上一页
          </button>
          <button
            class="secondary-button"
            type="button"
            :disabled="pagination.page >= totalPages || userLoading"
            @click="emit('goToPage', pagination.page + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </section>
</template>
