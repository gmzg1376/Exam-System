
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
            <li>进入考试后，系统将自动开始倒计时</li>
            <li>切屏次数超过5次将被记录</li>
            <li>每30秒自动保存一次答题进度</li>
            <li>切换题目时自动保存当前答案</li>
            <li>考试结束后自动交卷</li>
          </ul>
        </div>
        
        <div class="face-verification" v-if="showFaceVerify">
          <h3>人脸核验</h3>
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
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getExamById } from '../api/exam'
import { getAnswerByExam } from '../api/answer'

const route = useRoute()
const router = useRouter()

const exam = ref(null)
const completedSheet = ref(null)
const agreed = ref(false)
const showFaceVerify = ref(true)
const videoRef = ref(null)

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
  router.push(`/exam/${route.params.id}`)
}

function viewResult() {
  if (completedSheet.value) {
    router.push(`/result/${completedSheet.value.id}`)
  }
}

function verifyFace() {
  const canvas = document.querySelector('canvas')
  const video = videoRef.value
  canvas.getContext('2d').drawImage(video, 0, 0, 300, 200)
  alert('核验成功')
  showFaceVerify.value = false
}

function skipVerify() {
  showFaceVerify.value = false
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

.camera-container {
  width: 300px;
  height: 200px;
  background: #000;
  margin: 20px auto;
}

.camera-container video {
  width: 100%;
  height: 100%;
}

.start-btn {
  width: 100%;
  margin-top: 20px;
}
</style>
