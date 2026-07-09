
<template>
  <div class="rules-container">
    <el-card class="rules-card">
      <h2>{{ exam?.title }}</h2>
      <el-divider />
      
      <div v-if="completedSheet" class="completed-section">
        <el-alert 
          type="success" 
          title="您已完成本次考试" 
          :description="`成绩：${completedSheet.score}分，用时：${formatTime(completedSheet.spendTime)}`"
          show-icon
        />
        <el-button type="success" size="large" class="start-btn" @click="viewResult">
          查看答题详情
        </el-button>
      </div>
      
      <template v-else>
        <div class="rules-list">
          <h3>考试须知</h3>
          <ul>
            <li>考试时长：{{ exam?.duration }}分钟</li>
            <li>开始时间：{{ formatTime(exam?.startTime) }}</li>
            <li>结束时间：{{ formatTime(exam?.endTime) }}</li>
            <li v-if="exam?.randomQuestion">题目顺序：随机打乱</li>
            <li v-if="exam?.randomOption">选项顺序：随机打乱</li>
          </ul>
        </div>
        
        <div class="rules-list">
          <h3>考试规则</h3>
          <ul>
            <li>考试只能作答一次，完成后无法重新作答</li>
<<<<<<< HEAD
=======
            <li>进入考试前建议完成人脸核验</li>
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
            <li>进入考试后，系统将自动开始倒计时</li>
            <li>切屏次数超过5次将被记录</li>
            <li>每30秒自动保存一次答题进度</li>
            <li>切换题目时自动保存当前答案</li>
            <li>考试结束后自动交卷</li>
          </ul>
        </div>
        
        <div class="face-verification" v-if="showFaceVerify">
          <h3>人脸核验</h3>
<<<<<<< HEAD
          <div class="camera-container">
            <video ref="videoRef" autoplay></video>
            <canvas ref="canvasRef" style="display:none"></canvas>
          </div>
          <el-button type="primary" @click="verifyFace">拍照核验</el-button>
          <el-button @click="skipVerify">跳过</el-button>
        </div>
        
        <el-button type="primary" class="start-btn" @click="startExam" :disabled="!agreed">
          我已阅读并同意以上规则，开始考试
        </el-button>
        <el-checkbox v-model="agreed">我已阅读并同意考试规则</el-checkbox>
=======
          <p class="face-tip" v-if="!faceEnrolled">
            首次考试将自动录入人脸，也可在「个人信息」中提前录入
          </p>
          <p class="face-tip success" v-else-if="faceVerified">
            已通过人脸核验
          </p>
          <div class="camera-container">
            <video ref="videoRef" autoplay playsinline muted></video>
            <canvas ref="canvasRef" style="display:none"></canvas>
            <div v-if="!cameraReady" class="camera-placeholder">
              {{ cameraError || '正在启动摄像头...' }}
            </div>
          </div>
          <div class="face-actions">
            <el-button type="primary" @click="handleVerifyFace" :loading="verifying" :disabled="!cameraReady">
              拍照核验
            </el-button>
            <el-button @click="skipVerify">跳过</el-button>
          </div>
        </div>
        
        <el-checkbox v-model="agreed" class="agree-checkbox">我已阅读并同意考试规则</el-checkbox>
        <el-button
          type="primary"
          class="start-btn"
          @click="startExam"
          :disabled="!agreed || questionCount === 0"
        >
          {{ questionCount === 0 ? '暂无题目，无法开始' : '开始考试' }}
        </el-button>
        <p v-if="!agreed && questionCount > 0" class="start-hint">请先勾选上方同意考试规则后再开始</p>
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
      </template>
    </el-card>
  </div>
</template>

<script setup>
<<<<<<< HEAD
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getExamById } from '../api/exam'
import { getAnswerByExam } from '../api/answer'
=======
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamById, getExamQuestions } from '../api/exam'
import { getAnswerByExam } from '../api/answer'
import { getCurrentUser } from '../api/auth'
import {
  getStoredFaceDescriptor,
  verifyFace,
  loadFaceModels,
  stopCameraStream
} from '../utils/faceVerify'
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1

const route = useRoute()
const router = useRouter()

const exam = ref(null)
<<<<<<< HEAD
const completedSheet = ref(null)
const agreed = ref(false)
const showFaceVerify = ref(true)
const videoRef = ref(null)
=======
const questionCount = ref(0)
const completedSheet = ref(null)
const agreed = ref(false)
const userId = ref(null)
const faceVerified = ref(false)
const faceEnrolled = ref(false)
const showFaceVerify = ref(true)
const cameraReady = ref(false)
const cameraError = ref('')
const verifying = ref(false)
const videoRef = ref(null)
const canvasRef = ref(null)
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1

function formatTime(time) {
  if (!time) return ''
  if (typeof time === 'number') {
    const m = Math.floor(time / 60)
    const s = time % 60
    return `${m}分${s}秒`
  }
  return new Date(time).toLocaleString('zh-CN')
}

function startExam() {
<<<<<<< HEAD
=======
  if (questionCount.value === 0) {
    ElMessage.error('该考试暂无题目，请联系管理员添加题目后再进入')
    return
  }
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
  router.push(`/exam/${route.params.id}`)
}

function viewResult() {
  if (completedSheet.value) {
    router.push(`/result/${completedSheet.value.id}`)
  }
}

<<<<<<< HEAD
function verifyFace() {
  const canvas = document.querySelector('canvas')
  const video = videoRef.value
  canvas.getContext('2d').drawImage(video, 0, 0, 300, 200)
  alert('核验成功')
  showFaceVerify.value = false
=======
async function initCamera() {
  cameraReady.value = false
  cameraError.value = ''
  await nextTick()

  if (!navigator.mediaDevices?.getUserMedia) {
    cameraError.value = '当前浏览器不支持摄像头'
    showFaceVerify.value = false
    return
  }

  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'user', width: { ideal: 640 }, height: { ideal: 480 } }
    })
    if (videoRef.value) {
      videoRef.value.srcObject = stream
      videoRef.value.onloadedmetadata = () => {
        videoRef.value?.play()
        cameraReady.value = true
      }
    }
  } catch {
    cameraError.value = '无法访问摄像头，请检查浏览器权限'
    showFaceVerify.value = false
    ElMessage.warning('摄像头不可用，已跳过人脸核验')
  }
}

async function handleVerifyFace() {
  const video = videoRef.value
  const canvas = canvasRef.value
  if (!video || !canvas) return

  verifying.value = true
  try {
    await loadFaceModels()
    const result = await verifyFace(video, userId.value)
    canvas.width = 300
    canvas.height = 200
    canvas.getContext('2d').drawImage(video, 0, 0, 300, 200)

    if (result.success) {
      faceVerified.value = true
      faceEnrolled.value = true
      ElMessage.success(result.message)
    } else {
      ElMessage.error(result.message)
    }
  } catch {
    ElMessage.error('人脸核验失败，请稍后重试')
  } finally {
    verifying.value = false
  }
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
}

function skipVerify() {
  showFaceVerify.value = false
<<<<<<< HEAD
}

onMounted(async () => {
  const answerRes = await getAnswerByExam(route.params.id)
  if (answerRes.code === 200 && answerRes.data) {
    completedSheet.value = answerRes.data
    return
  }
  
  const res = await getExamById(route.params.id)
  if (res.code === 200) {
    exam.value = res.data
  }
  
  navigator.mediaDevices.getUserMedia({ video: true })
    .then(stream => {
      if (videoRef.value) {
        videoRef.value.srcObject = stream
      }
    })
    .catch(() => {
      showFaceVerify.value = false
    })
=======
  ElMessage.info('已跳过人脸核验')
}

onMounted(async () => {
  try {
    const userRes = await getCurrentUser()
    if (userRes.code === 200 && userRes.data) {
      userId.value = userRes.data.id
      faceEnrolled.value = !!getStoredFaceDescriptor(userRes.data.id)
    }

    const answerRes = await getAnswerByExam(route.params.id)
    if (answerRes.code === 200 && answerRes.data) {
      completedSheet.value = answerRes.data
      return
    }

    const res = await getExamById(route.params.id)
    if (res.code === 200) {
      exam.value = res.data
    } else {
      ElMessage.error(res.message || '考试不存在')
      router.push('/')
      return
    }

    const questionRes = await getExamQuestions(route.params.id)
    if (questionRes.code === 200) {
      questionCount.value = questionRes.data?.length || 0
      if (questionCount.value === 0) {
        ElMessage.warning('该考试尚未配置题目')
      }
    } else {
      ElMessage.error(questionRes.message || '加载题目失败')
    }
  } catch {
    ElMessage.error('加载考试信息失败，请检查网络后重试')
    router.push('/')
    return
  }

  await initCamera()
})

onBeforeUnmount(() => {
  stopCameraStream(videoRef.value)
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
})
</script>

<style scoped>
.rules-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  padding: 40px;
}

.completed-section {
  text-align: center;
  padding: 40px 0;
}

.completed-section .el-alert {
  margin-bottom: 30px;
}

.rules-card {
  width: 600px;
}

.rules-card h2 {
  text-align: center;
}

.rules-list {
  margin-bottom: 20px;
}

.rules-list h3 {
  color: #303133;
}

.rules-list ul {
  list-style: none;
  padding: 0;
}

.rules-list li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

<<<<<<< HEAD
.camera-container {
  width: 300px;
  height: 200px;
  background: #000;
  margin: 20px auto;
=======
.face-verification {
  margin-bottom: 20px;
}

.face-tip {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
}

.face-tip.success {
  color: #67c23a;
}

.camera-container {
  position: relative;
  width: 300px;
  height: 200px;
  background: #000;
  margin: 0 auto 12px;
  overflow: hidden;
  border-radius: 8px;
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
}

.camera-container video {
  width: 100%;
  height: 100%;
<<<<<<< HEAD
=======
  object-fit: cover;
}

.camera-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  padding: 12px;
  text-align: center;
  background: rgba(0, 0, 0, 0.75);
}

.face-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.agree-checkbox {
  display: block;
  margin-top: 20px;
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
}

.start-btn {
  width: 100%;
<<<<<<< HEAD
  margin-top: 20px;
=======
  margin-top: 12px;
}

.start-hint {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
  text-align: center;
>>>>>>> 0da6e3cd8bf9b64a37eefee18f8b298e24c273d1
}
</style>
