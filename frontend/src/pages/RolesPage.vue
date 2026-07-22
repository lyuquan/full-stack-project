<script setup>
// RolesPage renders the role-management route.
// App.vue still owns request state and passes role data into this page.
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
  selectedRole: Object
})

// Emits notify App.vue to run API requests or clear page state.
const emit = defineEmits(['loadRoles', 'loadRoleDetail', 'clearRoleDetail'])
</script>

<template>
  <section class="panel roles-panel">
    <div class="panel-header">
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
              <button
                class="link-button"
                type="button"
                @click="emit('loadRoleDetail', role.code)"
              >
                查看
              </button>
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
