<script setup>
// RolesPage renders the role-management route.
// App.vue owns request state and passes role data/form objects into this page.
defineOptions({
  inheritAttrs: false
})

// Props are data passed from App.vue to this route page.
defineProps({
  roleLoading: Boolean,
  roleError: String,
  roles: Array,
  roleDetailLoading: Boolean,
  roleDetailError: String,
  selectedRole: Object,
  roleForm: Object,
  roleSaveLoading: Boolean,
  roleSaveMessage: String,
  isEditingRole: Boolean,
  editingRoleCode: String,
  roleDeleteLoadingCode: String,
  permissionLoading: Boolean,
  permissionError: String,
  permissions: Array
})

// Emits notify App.vue to run API requests or clear page state.
const emit = defineEmits([
  'loadRoles',
  'loadRoleDetail',
  'clearRoleDetail',
  'saveRole',
  'resetRoleForm',
  'startEditRole',
  'deleteRole',
  'loadPermissions'
])
</script>

<template>
  <section class="panel roles-panel">
    <div class="panel-header">
      <div>
        <p class="eyebrow">
          {{ isEditingRole ? `PUT /api/roles/${editingRoleCode}` : 'POST /api/roles' }}
        </p>
        <h2>{{ isEditingRole ? '编辑角色' : '新增角色' }}</h2>
      </div>
    </div>

    <form class="form-grid" @submit.prevent="emit('saveRole')">
      <label>
        <span>角色编码</span>
        <input
          v-model="roleForm.code"
          :disabled="isEditingRole"
          placeholder="report_admin"
        />
      </label>

      <label>
        <span>角色名称</span>
        <input v-model="roleForm.name" placeholder="报表管理员" />
      </label>

      <label>
        <span>权限数量</span>
        <input v-model.number="roleForm.permissionCount" min="0" max="999" type="number" />
      </label>

      <label class="wide-field">
        <span>角色说明</span>
        <input v-model="roleForm.description" placeholder="用于说明这个角色能做什么" />
      </label>

      <div class="form-actions">
        <button class="submit-button" type="submit" :disabled="roleSaveLoading">
          {{ roleSaveLoading ? '保存中...' : isEditingRole ? '保存修改' : '新增角色' }}
        </button>
        <button class="secondary-button" type="button" @click="emit('resetRoleForm')">
          {{ isEditingRole ? '取消编辑' : '清空' }}
        </button>
      </div>
    </form>

    <p v-if="roleSaveMessage" class="status success form-message">
      {{ roleSaveMessage }}
    </p>

    <section class="permission-panel">
      <div class="panel-header">
        <div>
          <p class="eyebrow">GET /api/auth/permissions</p>
          <h2>权限字典</h2>
        </div>
        <button class="secondary-button" type="button" @click="emit('loadPermissions')">
          刷新权限
        </button>
      </div>

      <p v-if="permissionLoading" class="status muted">正在请求权限字典...</p>

      <p v-else-if="permissionError" class="status error">
        {{ permissionError }}
      </p>

      <div v-else class="table-wrap">
        <table class="compact-table">
          <thead>
            <tr>
              <th>权限编码</th>
              <th>权限名称</th>
              <th>权限说明</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="permission in permissions" :key="permission.code">
              <td>{{ permission.code }}</td>
              <td>{{ permission.name }}</td>
              <td>{{ permission.description }}</td>
            </tr>

            <tr v-if="permissions.length === 0">
              <td class="empty-cell" colspan="3">没有权限数据</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <div class="panel-header role-list-header">
      <div>
        <p class="eyebrow">GET /api/roles</p>
        <h2>角色管理</h2>
      </div>
      <button class="secondary-button" type="button" @click="emit('loadRoles')">
        刷新角色
      </button>
    </div>

    <p v-if="roleLoading" class="status muted">正在请求角色列表...</p>

    <p v-else-if="roleError" class="status error">
      {{ roleError }}
    </p>

    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>角色编码</th>
            <th>角色名称</th>
            <th>角色说明</th>
            <th>权限数量</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="role in roles" :key="role.code">
            <td>{{ role.code }}</td>
            <td>{{ role.name }}</td>
            <td>{{ role.description }}</td>
            <td>{{ role.permissionCount }}</td>
            <td>
              <div class="row-actions">
                <button
                  class="link-button"
                  type="button"
                  @click="emit('loadRoleDetail', role.code)"
                >
                  查看
                </button>
                <button
                  class="link-button"
                  type="button"
                  @click="emit('startEditRole', role)"
                >
                  编辑
                </button>
                <button
                  class="danger-link-button"
                  type="button"
                  :disabled="roleDeleteLoadingCode === role.code"
                  @click="emit('deleteRole', role)"
                >
                  {{ roleDeleteLoadingCode === role.code ? '删除中...' : '删除' }}
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="roles.length === 0">
            <td class="empty-cell" colspan="5">没有角色数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <section class="detail-panel role-detail-panel">
      <div class="panel-header">
        <div>
          <p class="eyebrow">GET /api/roles/{code}</p>
          <h3>角色详情</h3>
        </div>
        <button
          v-if="selectedRole || roleDetailError"
          class="secondary-button"
          type="button"
          @click="emit('clearRoleDetail')"
        >
          关闭详情
        </button>
      </div>

      <p v-if="roleDetailLoading" class="status muted">正在请求角色详情...</p>

      <p v-else-if="roleDetailError" class="status error">
        {{ roleDetailError }}
      </p>

      <div v-else-if="selectedRole" class="detail-grid">
        <dl>
          <dt>角色编码</dt>
          <dd>{{ selectedRole.code }}</dd>
        </dl>
        <dl>
          <dt>角色名称</dt>
          <dd>{{ selectedRole.name }}</dd>
        </dl>
        <dl>
          <dt>权限数量</dt>
          <dd>{{ selectedRole.permissionCount }}</dd>
        </dl>
        <dl>
          <dt>角色说明</dt>
          <dd>{{ selectedRole.description }}</dd>
        </dl>
      </div>

      <p v-else class="status muted">
        点击表格里的“查看”，会调用角色详情接口并在这里显示结果。
      </p>
    </section>
  </section>
</template>
