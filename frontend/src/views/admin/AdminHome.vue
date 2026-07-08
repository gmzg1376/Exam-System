
<template>
  <div class="admin-container">
    <el-container>
      <el-aside width="200px" class="admin-sidebar">
        <div class="sidebar-header">
          <h3>管理后台</h3>
        </div>
        <el-menu :default-active="activeMenu" class="admin-menu">
          <el-menu-item index="exams" @click="$router.push('/admin/exams')">
            <el-icon><Document /></el-icon>
            考试管理
          </el-menu-item>
          <el-menu-item index="questions" @click="$router.push('/admin/questions')">
            <el-icon><EditPen /></el-icon>
            题库管理
          </el-menu-item>
          <el-menu-item index="results" @click="$router.push('/admin/results')">
            <el-icon><DataAnalysis /></el-icon>
            成绩管理
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="admin-header">
          <span class="header-title">在线考试系统 - 管理后台</span>
          <div class="header-right">
            <span class="username">{{ userStore.user?.username }}</span>
            <el-button 
              type="danger" 
              size="large" 
              @click="handleLogout" 
              icon="SwitchButton"
              class="logout-btn"
            >
              退出登录
            </el-button>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => {
  const path = route.path
  if (path.includes('exams')) return 'exams'
  if (path.includes('questions')) return 'questions'
  if (path.includes('results')) return 'results'
  return 'exams'
})

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
}

.admin-sidebar {
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.admin-menu {
  border-right: none;
}

.admin-menu :deep(.el-menu-item) {
  color: #606266;
  font-size: 14px;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: #f5f7fa;
}

.admin-menu :deep(.is-active) {
  background: #ecf5ff;
  color: #409eff;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
  color: #606266;
}

.logout-btn {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: bold;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
  transition: all 0.3s ease;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(245, 108, 108, 0.4);
}
</style>
