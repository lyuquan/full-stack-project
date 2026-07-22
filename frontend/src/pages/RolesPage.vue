<script setup>
// RolesPage renders the role-management route.
// App.vue still owns the request state and passes role data into this page.
defineOptions({
  inheritAttrs: false
})

defineProps({
  roleLoading: Boolean,
  roleError: String,
  roles: Array
})

const emit = defineEmits(['loadRoles'])
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
          </tr>
        </thead>
        <tbody>
          <tr v-for="role in roles" :key="role.code">
            <td>{{ role.code }}</td>
            <td>{{ role.name }}</td>
            <td>{{ role.description }}</td>
            <td>{{ role.permissionCount }}</td>
          </tr>

          <tr v-if="roles.length === 0">
            <td class="empty-cell" colspan="4">没有角色数据</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
