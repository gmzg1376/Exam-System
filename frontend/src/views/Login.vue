<template>
  <div class="login-container">
    <div class="background-layer"></div>
    <el-card class="login-card">
      <h2>在线考试系统</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <p class="register-link">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { login } from '../api/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!await formRef.value.validate()) return
  
  loading.value = true
  try {
    const res = await login(form)
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUser(res.data.user)
      if (res.data.user.role === 'ADMIN') {
        router.push('/admin/exams')
      } else {
        router.push('/')
      }
    } else {
      alert(res.message)
    }
  } catch (error) {
    alert('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.background-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: blur(5px);
  transform: scale(1.05);
}

.login-card {
  width: 400px;
  padding: 40px;
  text-align: center;
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-card h2 {
  margin-bottom: 30px;
  color: #303133;
}

.login-btn {
  width: 100%;
}

.register-link {
  margin-top: 20px;
  color: #909399;
}

.register-link a {
  color: #409eff;
}
</style>