<template>
  <div class="user-profile">
    <div class="profile-header">
      <h2 class="page-title">个人信息</h2>
      <el-button type="primary" @click="$router.push('/')" icon="House">
        返回首页
      </el-button>
    </div>
    
    <el-card class="profile-card">
      <div class="profile-main">
        <div class="avatar-section">
          <div class="avatar-container" @click="showAvatarUpload = true">
            <el-avatar :size="120" :src="userInfo.avatar" icon="User" class="avatar" />
            <div class="avatar-edit">
              <el-icon class="edit-icon"><Camera /></el-icon>
            </div>
          </div>
          <p class="avatar-tip">点击更换头像</p>
        </div>
        
        <div class="profile-info">
          <h2 class="username">{{ userInfo.username }}</h2>
          <p class="role">{{ userInfo.role === 'ADMIN' ? '管理员' : '学生' }}</p>
        </div>
      </div>
      
      <el-divider />
      
      <el-descriptions :column="2" border class="user-details">
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">
          <span v-if="!editingEmail">{{ userInfo.email || '-' }}</span>
          <el-input v-else v-model="editForm.email" size="small" />
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          <span v-if="!editingPhone">{{ userInfo.phone || '-' }}</span>
          <el-input v-else v-model="editForm.phone" size="small" />
        </el-descriptions-item>
        <el-descriptions-item label="账号ID">{{ userInfo.id }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(userInfo.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(userInfo.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="action-buttons">
        <el-button type="primary" @click="showChangePassword = true" icon="Lock">
          修改密码
        </el-button>
        <el-button type="warning" @click="openFaceEnroll" icon="Camera">
          {{ faceEnrolled ? '重新录入人脸' : '录入人脸' }}
        </el-button>
        <el-button type="success" @click="saveProfile" icon="Check">
          保存修改
        </el-button>
      </div>

      <el-alert
        v-if="faceEnrolled"
        type="success"
        title="人脸信息已录入"
        description="进入考试时可进行人脸比对核验"
        show-icon
        :closable="false"
        class="face-status-alert"
      />
      
      <el-divider />
      
      <div class="exam-section">
        <h3>参与的考试</h3>
        <el-table :data="examHistory" border v-loading="loading">
          <el-table-column prop="examId" label="考试ID" width="100" />
          <el-table-column prop="examTitle" label="考试名称">
            <template #default="scope">
              <el-button type="text" @click="viewExamResult(scope.row)" class="exam-name-link">
                {{ scope.row.examTitle }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="成绩" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.score >= 60 ? 'success' : 'danger'">
                {{ scope.row.score }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '已提交' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="spendTime" label="用时" width="120">
            <template #default="scope">
              {{ formatTime(scope.row.spendTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="submitTime" label="提交时间">
            <template #default="scope">
              {{ formatDateTime(scope.row.submitTime) }}
            </template>
          </el-table-column>
        </el-table>
        <div v-if="examHistory.length === 0 && !loading" class="empty-tip">
          <el-empty description="暂无考试记录" />
        </div>
      </div>
    </el-card>
    
    <el-dialog v-model="showAvatarUpload" title="更换头像" width="400px">
      <div class="avatar-upload">
        <el-upload
          class="avatar-uploader"
          action="/api/user/upload-avatar"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <el-avatar :size="100" :src="userInfo.avatar" icon="User" class="preview-avatar" />
        </el-upload>
        <p class="upload-tip">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
      </div>
    </el-dialog>
    
    <el-dialog v-model="showFaceEnroll" title="人脸录入" width="420px" @closed="closeFaceEnroll">
      <p class="face-enroll-tip">请正对摄像头，确保光线充足、面部清晰可见</p>
      <div class="face-enroll-camera">
        <video ref="faceVideoRef" autoplay playsinline muted></video>
        <div v-if="!faceCameraReady" class="camera-placeholder">正在启动摄像头...</div>
      </div>
      <template #footer>
        <el-button @click="showFaceEnroll = false">取消</el-button>
        <el-button type="primary" @click="handleEnrollFace" :loading="faceEnrolling" :disabled="!faceCameraReady">
          确认录入
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showChangePassword" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '../api/auth'
import { getAnswerHistory } from '../api/answer'
import { getExams } from '../api/exam'
import { updateUserInfo, changePassword as apiChangePassword } from '../api/user'
import { Camera, House, Lock, Check } from '@element-plus/icons-vue'
import {
  getStoredFaceDescriptor,
  enrollFace,
  stopCameraStream
} from '../utils/faceVerify'

const router = useRouter()
const userInfo = ref({})
const examHistory = ref([])
const loading = ref(false)
const examsMap = ref(new Map())
const examSheetMap = ref(new Map())

const showAvatarUpload = ref(false)
const showChangePassword = ref(false)
const showFaceEnroll = ref(false)
const faceEnrolled = ref(false)
const faceCameraReady = ref(false)
const faceEnrolling = ref(false)
const faceVideoRef = ref(null)
const editingEmail = ref(false)
const editingPhone = ref(false)

const editForm = ref({
  email: '',
  phone: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

function formatTime(seconds) {
  if (!seconds) return '-'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}分${s}秒`
}

function formatDateTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function viewExamResult(row) {
  router.push(`/result/${row.id}`)
}

async function loadUserInfo() {
  const res = await getCurrentUser()
  if (res.code === 200) {
    userInfo.value = res.data
    editForm.value = {
      email: res.data.email || '',
      phone: res.data.phone || ''
    }
    faceEnrolled.value = !!getStoredFaceDescriptor(res.data.id)
  }
}

async function openFaceEnroll() {
  showFaceEnroll.value = true
  faceCameraReady.value = false
  await nextTick()
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'user', width: { ideal: 640 }, height: { ideal: 480 } }
    })
    if (faceVideoRef.value) {
      faceVideoRef.value.srcObject = stream
      faceVideoRef.value.onloadedmetadata = () => {
        faceVideoRef.value?.play()
        faceCameraReady.value = true
      }
    }
  } catch {
    ElMessage.error('无法访问摄像头，请检查浏览器权限')
    showFaceEnroll.value = false
  }
}

function closeFaceEnroll() {
  stopCameraStream(faceVideoRef.value)
  faceCameraReady.value = false
}

async function handleEnrollFace() {
  if (!faceVideoRef.value || !userInfo.value.id) return
  faceEnrolling.value = true
  try {
    const result = await enrollFace(faceVideoRef.value, userInfo.value.id)
    if (result.success) {
      faceEnrolled.value = true
      ElMessage.success(result.message)
      showFaceEnroll.value = false
    } else {
      ElMessage.error(result.message)
    }
  } catch {
    ElMessage.error('人脸录入失败，请稍后重试')
  } finally {
    faceEnrolling.value = false
  }
}

async function loadExamHistory() {
  loading.value = true
  try {
    const [examsRes, historyRes] = await Promise.all([
      getExams(),
      getAnswerHistory()
    ])
    
    if (examsRes.code === 200) {
      examsRes.data.forEach(e => {
        examsMap.value.set(e.id, e.title)
      })
    }
    
    if (historyRes.code === 200) {
      examHistory.value = historyRes.data.map(item => ({
        ...item,
        examTitle: examsMap.value.get(item.examId) || `考试ID: ${item.examId}`
      }))
    }
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  try {
    const res = await updateUserInfo(editForm.value)
    if (res.code === 200) {
      userInfo.value = { ...userInfo.value, ...editForm.value }
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error(e?.message || '保存失败，请稍后重试')
  }
}

async function changePassword() {
  if (!passwordForm.value.oldPassword) {
    ElMessage.error('请输入原密码')
    return
  }
  if (!passwordForm.value.newPassword) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  try {
    const res = await apiChangePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      showChangePassword.value = false
      passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    } else {
      ElMessage.error(res.message || '密码修改失败')
    }
  } catch (e) {
    ElMessage.error(e?.message || '密码修改失败，请稍后重试')
  }
}

function handleAvatarSuccess(res) {
  if (res.code === 200) {
    userInfo.value.avatar = res.data
    showAvatarUpload.value = false
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(res.message || '头像上传失败')
  }
}

function beforeAvatarUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式')
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB')
  }
  return isJPG && isLt2M
}

onMounted(async () => {
  await loadUserInfo()
  await loadExamHistory()
})
</script>

<style scoped>
.user-profile {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #303133;
}

.profile-card {
  border-radius: 12px;
}

.profile-main {
  display: flex;
  align-items: center;
  gap: 30px;
  padding-bottom: 20px;
}

.avatar-section {
  flex-shrink: 0;
  text-align: center;
}

.avatar-container {
  position: relative;
  cursor: pointer;
  display: inline-block;
}

.avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 48px;
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  padding: 8px;
}

.edit-icon {
  color: white;
  font-size: 16px;
}

.avatar-tip {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
}

.profile-info {
  flex: 1;
}

.username {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #303133;
}

.role {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.user-details {
  margin: 20px 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.face-status-alert {
  margin-bottom: 20px;
}

.face-enroll-tip {
  margin: 0 0 12px;
  font-size: 13px;
  color: #606266;
}

.face-enroll-camera {
  position: relative;
  width: 100%;
  height: 240px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.face-enroll-camera video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.face-enroll-camera .camera-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: rgba(0, 0, 0, 0.7);
}

.exam-section {
  margin-top: 20px;
}

.exam-section h3 {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 16px 0;
  color: #303133;
}

.exam-name-link {
  color: #409eff;
  font-weight: bold;
}

.exam-name-link:hover {
  text-decoration: underline;
}

.empty-tip {
  padding: 40px;
}

.avatar-upload {
  text-align: center;
}

.preview-avatar {
  margin: 0 auto;
  font-size: 40px;
}

.upload-tip {
  margin-top: 16px;
  font-size: 14px;
  color: #909399;
}
</style>